package com.cornershop.counterstest.welcome.presentation

import android.os.Bundle
import androidx.lifecycle.Observer
import com.cornershop.counterstest.counter.presentation.CountersActivity
import com.cornershop.counterstest.databinding.WelcomeActivityBinding
import com.cornershop.counterstest.utils.extensions.clearBackStackFlags
import com.cornershop.counterstest.utils.extensions.viewBinding
import com.cornershop.counterstest.welcome.viewmodel.WelcomeViewModel
import org.koin.androidx.scope.ScopeActivity
import org.koin.androidx.viewmodel.ext.android.viewModel


class WelcomeActivity : ScopeActivity() {

    private val binding by viewBinding(WelcomeActivityBinding::inflate)
    private val viewModel: WelcomeViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        binding.welcomeContent?.buttonStart?.setOnClickListener {
            navigateToHome()
        }
    }

    private fun setupObservers() {
        viewModel.model.observe(this, Observer(::updateUI))
    }

    private fun updateUI(model: WelcomeViewModel.UiModel) {
        if (!model.isFirstTimeState) {
            navigateToHome()
        }
    }

    private fun navigateToHome() {
        val intent = CountersActivity.newInstance(this).clearBackStackFlags()
        startActivity(intent)
    }
}