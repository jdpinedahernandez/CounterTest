package com.cornershop.counterstest.counter.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.cornershop.counterstest.counter.data.CounterRequest
import com.cornershop.counterstest.counter.domain.CountersInteractor
import com.cornershop.counterstest.counter.model.Counter
import com.cornershop.counterstest.utils.data.onError
import com.cornershop.counterstest.utils.data.onSuccess
import com.cornershop.counterstest.utils.ui.ScopedViewModel
import kotlinx.coroutines.CoroutineDispatcher

class CountersViewModel(
    private val interactor: CountersInteractor,
    uiDispatcher: CoroutineDispatcher
) : ScopedViewModel(uiDispatcher) {

    private var counters: List<Counter> = emptyList()
    private val _warnAboutConnection = MutableLiveData<Unit>()

    var counterToUpdate: Counter? = null
    val warnAboutConnection: LiveData<Unit> get() = _warnAboutConnection

    sealed class UiModel {
        object Loading : UiModel()
        data class ShowCounters(val list: List<Counter>) : UiModel()
        data class ShowCountersError(val exception: Throwable) : UiModel()
        data class DeleteError(val exception: Throwable) : UiModel()
        object NoCountersError : UiModel()
        data class ShowSelectedCounters(val list: List<Counter>) : UiModel()
    }

    private val _model = MutableLiveData<UiModel>()
    val model: LiveData<UiModel> = _model

    fun getCounters() = launch {
        interactor.getCounters()
            .onSuccess {
                counters = it
                _model.value = if (it.isEmpty()) UiModel.NoCountersError
                else UiModel.ShowCounters(it)
            }
            .onError {
                _model.value = UiModel.ShowCountersError(failure)
            }
    }

    fun incrementCounter(counter: Counter) = launch {
        _model.value = UiModel.Loading
        counterToUpdate = counter.copy(count = counter.count + 1)
        interactor.incrementCounter(counter.id)
            .onSuccess {
                counterToUpdate = null
                _model.value = UiModel.ShowCounters(it)
            }
            .onError {
                _model.value = UiModel.ShowCountersError(failure)
            }
    }

    fun decrementCounter(counter: Counter) = launch {
        _model.value = UiModel.Loading
        counterToUpdate = counter.copy(count = counter.count - 1)
        interactor.decrementCounter(counter.id)
            .onSuccess {
                counterToUpdate = null
                _model.value = UiModel.ShowCounters(it)
            }
            .onError {
                _model.value = UiModel.ShowCountersError(failure)
            }
    }

    fun deleteSelectedCounters() = launch {
        getSelectedCounters().map { counter ->
            interactor.deleteCounter(counter.id)
                .onSuccess {
                    clearSelectedCounters()
                    _model.value = UiModel.ShowCounters(it)
                }
                .onError {
                    _model.value = UiModel.DeleteError(failure)
                }
        }
    }

    fun filterByQuery(query: String) =
        counters.filter { counter ->
            counter.title.contains(query, ignoreCase = true) || query.isBlank()
        }.run {
            _model.value =
                if (isEmpty() && counters.isNotEmpty()) UiModel.NoCountersError
                else UiModel.ShowCounters(this)
        }

    fun toggleSelectedCounter(counter: Counter) {
        counters.find { it.id == counter.id }.also { it?.isSelected = counter.isSelected }
        _model.value = UiModel.ShowSelectedCounters(getSelectedCounters())
    }

    fun clearSelectedCounters() {
        counters.forEach { it.isSelected = false }
        _model.postValue(UiModel.ShowSelectedCounters(mutableListOf()))
    }

    fun getSelectedCounters() = counters.filter { it.isSelected }
}