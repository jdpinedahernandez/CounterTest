package com.cornershop.counterstest.counters.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.cornershop.counterstest.counter.domain.CountersInteractor
import com.cornershop.counterstest.counter.model.Counter
import com.cornershop.counterstest.counter.viewmodel.CreateCounterViewModel
import com.cornershop.counterstest.utils.data.ErrorResponse
import com.cornershop.counterstest.utils.data.NetworkError
import com.cornershop.counterstest.utils.data.SuccessResponse
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class CreateCounterViewModelUnitTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Mock
    lateinit var mockInteractor: CountersInteractor

    @Mock
    lateinit var observer: Observer<CreateCounterViewModel.UiModel>

    private lateinit var viewModel: CreateCounterViewModel

    @Before
    fun setUp() {
        viewModel = CreateCounterViewModel(mockInteractor, Dispatchers.Unconfined)
    }

    @Test
    fun `should post counters value with success`() {
        runBlocking {
            // given
            val counterTitle = "title"
            val expected = listOf(Counter("id1", "title1", 1))
            viewModel.model.observeForever(observer)
            whenever(mockInteractor.addCounter(any())).thenReturn(SuccessResponse(expected))

            // when
            viewModel.createCounter(counterTitle)

            verify(observer).onChanged(CreateCounterViewModel.UiModel.Loading)
            verify(observer).onChanged(CreateCounterViewModel.UiModel.CounterAdded(expected))
        }
    }

    @Test
    fun `should post counters value with error`() {
        runBlocking {
            // given
            val counterTitle = "title"
            val expected = NetworkError
            viewModel.model.observeForever(observer)
            whenever(mockInteractor.addCounter(any())).thenReturn(ErrorResponse(expected))

            // when
            viewModel.createCounter(counterTitle)

            verify(observer).onChanged(CreateCounterViewModel.UiModel.Loading)
            verify(observer).onChanged(CreateCounterViewModel.UiModel.ShowError(expected))
        }
    }
}