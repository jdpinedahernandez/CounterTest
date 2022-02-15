package com.cornershop.counterstest.counter.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.cornershop.counterstest.counter.domain.CountersInteractor
import com.cornershop.counterstest.counter.model.Counter
import com.cornershop.counterstest.utils.data.onError
import com.cornershop.counterstest.utils.data.onSuccess
import com.cornershop.counterstest.utils.ui.ScopedViewModel
import kotlinx.coroutines.CoroutineDispatcher

class CreateCounterViewModel(
    private val interactor: CountersInteractor,
    uiDispatcher: CoroutineDispatcher
) : ScopedViewModel(uiDispatcher) {

    sealed class UiModel {
        object Loading : UiModel()
        data class CounterAdded(val list: List<Counter>) : UiModel()
        data class ShowError(val exception: Throwable) : UiModel()
    }

    private val _model = MutableLiveData<UiModel>()
    val model: LiveData<UiModel> = _model

    fun createCounter(title: String) = launch {
        _model.value = UiModel.Loading
        interactor.addCounter(title)
            .onSuccess { _model.value = UiModel.CounterAdded(it) }
            .onError { _model.value = UiModel.ShowError(failure) }
    }
}