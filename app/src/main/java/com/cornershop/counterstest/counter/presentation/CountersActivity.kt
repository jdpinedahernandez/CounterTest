package com.cornershop.counterstest.counter.presentation

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.cornershop.counterstest.R
import com.cornershop.counterstest.counter.model.Counter
import com.cornershop.counterstest.counter.presentation.entries.CounterEmptyEntry
import com.cornershop.counterstest.counter.presentation.entries.CounterEntryHandler
import com.cornershop.counterstest.counter.viewmodel.CountersViewModel
import com.cornershop.counterstest.counter.viewmodel.CountersViewModel.UiModel.DeleteError
import com.cornershop.counterstest.counter.viewmodel.CountersViewModel.UiModel.Loading
import com.cornershop.counterstest.counter.viewmodel.CountersViewModel.UiModel.NoCountersError
import com.cornershop.counterstest.counter.viewmodel.CountersViewModel.UiModel.ShowCounters
import com.cornershop.counterstest.counter.viewmodel.CountersViewModel.UiModel.ShowCountersError
import com.cornershop.counterstest.counter.viewmodel.CountersViewModel.UiModel.ShowSelectedCounters
import com.cornershop.counterstest.databinding.CountersActivityBinding
import com.cornershop.counterstest.utils.data.NetworkError
import com.cornershop.counterstest.utils.extensions.getString
import com.cornershop.counterstest.utils.extensions.observe
import com.cornershop.counterstest.utils.extensions.observeTextChange
import com.cornershop.counterstest.utils.extensions.snackBar
import com.cornershop.counterstest.utils.extensions.textShareIntent
import com.cornershop.counterstest.utils.extensions.viewBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.xwray.groupie.GroupieAdapter
import org.koin.androidx.scope.ScopeActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class CountersActivity : ScopeActivity(), CounterEntryHandler {

    private val binding by viewBinding(CountersActivityBinding::inflate)
    private val viewModel: CountersViewModel by viewModel()
    private val groupAdapter by lazy { GroupieAdapter() }
    private var onRetryChangeCounterCount: () -> Unit = { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupViews()
        setupObservers()
        getCounters()
    }

    private fun setupViews() = with(binding) {
        recyclerView.adapter = groupAdapter
        addCounter.setOnClickListener { navigateToAddCounter() }
        swipeRefresh.apply {
            setColorSchemeResources(R.color.orange)
            setOnRefreshListener { getCounters() }
        }
        toolbar.apply {
            setNavigationOnClickListener { deleteCounters() }
            setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_share -> shareCounters().let { true }
                    else -> false
                }
            }
        }
        search.observeTextChange(viewModel::filterByQuery)
    }

    private fun setupObservers() = with(viewModel) {
        viewModel.model.observe(this@CountersActivity, Observer(::updateUI))
        observe(warnAboutConnection, ::warnAboutConnection)
    }

    private fun getCounters() {
        viewModel.clearSelectedCounters()
        viewModel.getCounters()
    }

    private fun updateUI(model: CountersViewModel.UiModel) {
        loading((model is Loading))
        when (model) {
            is ShowCounters -> showCounters(model.list)
            is ShowCountersError -> showErrorAsAlert(
                error = model.exception,
                onRetry = onRetryChangeCounterCount
            )
            is DeleteError -> showErrorAsAlert(
                error = model.exception,
                onRetry = ::deleteCounters,
                isDeletion = true
            )
            is ShowSelectedCounters -> onSelectedCountersChanged(model.list)
            NoCountersError -> onCounterNotFoundChanged(true)
        }
    }

    private fun onSelectedCountersChanged(counters: List<Counter>) = with(binding) {
        search.isVisible = counters.isEmpty()
        toolbar.isVisible = counters.isNotEmpty()
        toolbar.title = getString(R.string.n_selected, counters.size)
        groupAdapter.notifyDataSetChanged()
    }

    private fun showCounters(counters: List<Counter>) {
        loading(false)
        groupAdapter.update(CountersPresentation(counters, this).entries)
    }

    private fun onCounterNotFoundChanged(counterNotFound: Boolean) {
        loading(false)
        if (counterNotFound) {
            groupAdapter.update(listOf(CounterEmptyEntry(isSearchingByQuery = true)))
        }
    }

    private fun warnAboutConnection(unit: Unit) {
        snackBar(R.string.connection_error_description)
    }

    private fun showError(error: Throwable, onRetry: () -> Unit) = with(binding) {
        loading(false)
        errorView.showError(error, onRetry)
    }

    private fun showErrorAsAlert(
        error: Throwable,
        onRetry: () -> Unit,
        isDeletion: Boolean = false
    ) {
        loading(false)
        val title = if (isDeletion) {
            getString(R.string.error_deleting_counter_title)
        } else {
            val (counterTitle, count) = viewModel.counterToUpdate.let { it?.title.orEmpty() to it?.count }
            getString(R.string.error_updating_counter_title, counterTitle, count)
        }

        val message = when (error) {
            NetworkError -> R.string.connection_error_description
            else -> R.string.error_default_description
        }

        MaterialAlertDialogBuilder(this, R.style.AlertDialogTheme)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(R.string.ok) { _, _ -> }
            .setNegativeButton(R.string.retry) { _, _ -> onRetry() }
            .create()
            .show()
    }

    private fun loading(isLoading: Boolean) = with(binding) {
        if (swipeRefresh.isRefreshing) {
            swipeRefresh.isRefreshing = isLoading
        } else {
            errorView.isVisible = false
            progressLoading.isVisible = isLoading
            recyclerView.isVisible = isLoading.not()
        }
    }

    private fun deleteCounters() {
        viewModel.deleteSelectedCounters()
    }

    private fun shareCounters() {
        val counters = viewModel.getSelectedCounters()
            ?.joinToString(
                separator = "\n",
                transform = { counter -> "${counter.count} x ${counter.title}" }
            ) ?: return

        val shareIntent = textShareIntent(counters, getString(R.string.share))
        startActivity(shareIntent)
    }

    override fun onBackPressed() {
        if (binding.appBar.isVisible) viewModel.clearSelectedCounters()
        else super.onBackPressed()
    }

    override fun onCounterIncrement(counter: Counter) {
        onRetryChangeCounterCount = { viewModel.incrementCounter(counter) }
            .also { it.invoke() }
    }

    override fun onCounterDecrement(counter: Counter) {
        onRetryChangeCounterCount = { viewModel.decrementCounter(counter) }
            .also { it.invoke() }
    }

    override fun onCounterSelected(counter: Counter) {
        groupAdapter.notifyDataSetChanged()
        viewModel.toggleSelectedCounter(counter)
    }

    private fun navigateToAddCounter() {
        val intent = CreateCounterActivity.newInstance(this)
        resultLauncher.launch(intent)
    }

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) getCounters()
        }

    companion object {
        fun newInstance(context: Context) = Intent(context, CountersActivity::class.java)
    }
}