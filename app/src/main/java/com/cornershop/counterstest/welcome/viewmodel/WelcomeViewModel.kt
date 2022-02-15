package com.cornershop.counterstest.welcome.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.cornershop.counterstest.utils.ui.ScopedViewModel
import com.cornershop.counterstest.welcome.data.WelcomeService
import kotlinx.coroutines.CoroutineDispatcher

class WelcomeViewModel(
    private val infrastructure: WelcomeService,
    uiDispatcher: CoroutineDispatcher
) : ScopedViewModel(uiDispatcher) {

    data class UiModel(val isFirstTimeState: Boolean)

    private val _model = MutableLiveData<UiModel>()
    val model: LiveData<UiModel> get() {
        if (_model.value == null) checkIsFirstTime()
        return _model
    }

    private fun checkIsFirstTime() = launch {
        _model.value = UiModel(infrastructure.isFirstTime())
    }
}