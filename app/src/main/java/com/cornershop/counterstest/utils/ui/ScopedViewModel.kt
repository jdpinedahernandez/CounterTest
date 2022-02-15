package com.cornershop.counterstest.utils.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

open class ScopedViewModel(private val uiDispatcher: CoroutineDispatcher) : ViewModel() {
    fun ViewModel.launch(block: suspend CoroutineScope.() -> Unit) {
        viewModelScope.launch(uiDispatcher) {
            block.invoke(this)
        }
    }
}