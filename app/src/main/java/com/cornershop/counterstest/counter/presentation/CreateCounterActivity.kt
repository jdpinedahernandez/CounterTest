package com.cornershop.counterstest.counter.presentation

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.cornershop.counterstest.R
import com.cornershop.counterstest.counter.domain.CounterBusiness
import com.cornershop.counterstest.counter.viewmodel.CreateCounterViewModel
import com.cornershop.counterstest.counter.viewmodel.CreateCounterViewModel.UiModel.CounterAdded
import com.cornershop.counterstest.counter.viewmodel.CreateCounterViewModel.UiModel.Loading
import com.cornershop.counterstest.counter.viewmodel.CreateCounterViewModel.UiModel.ShowError
import com.cornershop.counterstest.databinding.CreateCounterActivityBinding
import com.cornershop.counterstest.utils.data.NetworkError
import com.cornershop.counterstest.utils.extensions.bold
import com.cornershop.counterstest.utils.extensions.getString
import com.cornershop.counterstest.utils.extensions.hideKeyboard
import com.cornershop.counterstest.utils.extensions.normal
import com.cornershop.counterstest.utils.extensions.plus
import com.cornershop.counterstest.utils.extensions.snackBar
import com.cornershop.counterstest.utils.extensions.underline
import com.cornershop.counterstest.utils.extensions.viewBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.scope.ScopeActivity
import org.koin.androidx.viewmodel.ext.android.viewModel


class CreateCounterActivity : ScopeActivity() {

    private val binding by viewBinding(CreateCounterActivityBinding::inflate)
    private val viewModel: CreateCounterViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupViews()
        setupObservers()
    }

    private fun setupViews() = with(binding) {
        val seeSuggestionsDisclaimer = normal(getString(R.string.create_counter_disclaimer)) +
                bold(underline(getString(R.string.create_counter_disclaimer_see_examples)))

        save.setOnClickListener { saveCounter() }
        close.setOnClickListener { finish() }
        seeSuggestions.apply {
            text = seeSuggestionsDisclaimer
            setOnClickListener { navigateToSeeSuggestions() }
        }
    }

    private fun setupObservers() {
        viewModel.model.observe(this@CreateCounterActivity, Observer(::updateUI))
    }

    private fun updateUI(model: CreateCounterViewModel.UiModel) = when (model) {
        Loading -> loading(true)
        is CounterAdded -> onCounterAddedWithSuccess()
        is ShowError -> showError(model.exception)
    }

    private fun saveCounter() =
        binding.counterTitleInput.editText?.text?.toString().orEmpty().apply {
            hideKeyboard()
            viewModel.createCounter(this)
        }

    private fun onCounterAddedWithSuccess() =
        snackBar(R.string.added_counter_with_success, onDismissed = {
            loading(false)
            setResult(RESULT_OK)
            finish()
        })

    private fun loading(isLoading: Boolean) = with(binding) {
        progressLoading.isVisible = isLoading
        save.isVisible = isLoading.not()
    }

    private fun showError(error: Throwable) {
        val (title, message) = when (error) {
            NetworkError -> R.string.error_creating_counter_title to R.string.connection_error_description
            CounterBusiness.InvalidEmptyTitle -> R.string.error_creating_counter_title to R.string.error_invalid_counter_empty_title
            CounterBusiness.InvalidTitleSize -> R.string.error_creating_counter_title to R.string.error_invalid_counter_title_size
            else -> R.string.error_default_title to R.string.error_default_description
        }

        loading(false)
        MaterialAlertDialogBuilder(this, R.style.AlertDialogTheme)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(R.string.ok) { _, _ -> }
            .create()
            .show()
    }

    private fun navigateToSeeSuggestions() {
        val intent = CreateCounterExamplesActivity.newInstance(this)
        resultLauncher.launch(intent)
    }

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.getStringExtra(CREATE_COUNTER_EXAMPLE_KEY)?.let { example ->
                    binding.counterTitleInput.editText?.setText(example)
                }
            }
        }

    companion object {
        const val CREATE_COUNTER_EXAMPLE_KEY = "create_counter_example_key"

        fun newInstance(context: Context) = Intent(context, CreateCounterActivity::class.java)
    }
}