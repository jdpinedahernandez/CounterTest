package com.cornershop.counterstest.counters.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.cornershop.counterstest.counter.domain.CountersInteractor
import com.cornershop.counterstest.counter.model.Counter
import com.cornershop.counterstest.counter.viewmodel.CountersViewModel
import com.cornershop.counterstest.utils.data.ErrorResponse
import com.cornershop.counterstest.utils.data.NetworkError
import com.cornershop.counterstest.utils.data.SuccessResponse
import com.cornershop.counterstest.utils.setFieldHelper
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

const val FIELD_COUNTERS = "counters"

@RunWith(MockitoJUnitRunner::class)
class CountersViewModelUnitTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Mock
    lateinit var mockInteractor: CountersInteractor

    @Mock
    lateinit var observer: Observer<CountersViewModel.UiModel>

    private lateinit var viewModel: CountersViewModel

    private val counters = listOf(
        Counter("id1", "title1", 0, isSelected = true),
        Counter("id2", "title2", 0, isSelected = false)
    )

    @Before
    fun setUp() {
        viewModel = CountersViewModel(mockInteractor, Dispatchers.Unconfined)
    }

    @Test
    fun `get counters value with success`() {
        runBlocking {
            // given
            val expected = listOf(Counter("id1", "title1", 1))
            viewModel.model.observeForever(observer)
            whenever(mockInteractor.getCounters()).thenReturn(SuccessResponse(expected))

            // when
            viewModel.getCounters()

            verify(observer).onChanged(CountersViewModel.UiModel.ShowCounters(expected))
        }
    }

    @Test
    fun `get counters value with success with empty list`() {
        runBlocking {
            // given
            val expected = listOf<Counter>()
            viewModel.model.observeForever(observer)
            whenever(mockInteractor.getCounters()).thenReturn(SuccessResponse(expected))

            // when
            viewModel.getCounters()

            verify(observer).onChanged(CountersViewModel.UiModel.NoCountersError)
        }
    }

    @Test
    fun `get counters value with error`() {
        runBlocking {
            // given
            val expected = NetworkError
            viewModel.model.observeForever(observer)
            whenever(mockInteractor.getCounters()).thenReturn(ErrorResponse(expected))

            // when
            viewModel.getCounters()

            verify(observer).onChanged(CountersViewModel.UiModel.ShowCountersError(expected))
        }
    }

    @Test
    fun `increment counter value with success`() {
        runBlocking {
            // given
            val counter = Counter("id1", "title1", 1)
            val expected = listOf(Counter("id1", "title1", 2))
            viewModel.model.observeForever(observer)
            whenever(mockInteractor.incrementCounter(any())).thenReturn(SuccessResponse(expected))

            // when
            viewModel.incrementCounter(counter)

            verify(observer).onChanged(CountersViewModel.UiModel.Loading)
            verify(observer).onChanged(CountersViewModel.UiModel.ShowCounters(expected))
        }
    }

    @Test
    fun `increment counter value with error`() {
        runBlocking {
            // given
            val counter = Counter("id1", "title1", 1)
            val expected = NetworkError
            viewModel.model.observeForever(observer)
            whenever(mockInteractor.incrementCounter(any())).thenReturn(ErrorResponse(expected))

            // when
            viewModel.incrementCounter(counter)

            verify(observer).onChanged(CountersViewModel.UiModel.Loading)
            verify(observer).onChanged(CountersViewModel.UiModel.ShowCountersError(expected))
        }
    }

    @Test
    fun `decrementCounter counter value with success`() {
        runBlocking {
            // given
            val counter = Counter("id1", "title1", 1)
            val expected = listOf(Counter("id1", "title1", 0))
            viewModel.model.observeForever(observer)
            whenever(mockInteractor.decrementCounter(any())).thenReturn(SuccessResponse(expected))

            // when
            viewModel.decrementCounter(counter)

            verify(observer).onChanged(CountersViewModel.UiModel.Loading)
            verify(observer).onChanged(CountersViewModel.UiModel.ShowCounters(expected))
        }
    }

    @Test
    fun `decrementCounter counter value with error`() {
        runBlocking {
            // given
            val counter = Counter("id1", "title1", 1)
            val expected = NetworkError
            viewModel.model.observeForever(observer)
            whenever(mockInteractor.decrementCounter(any())).thenReturn(ErrorResponse(expected))

            // when
            viewModel.decrementCounter(counter)

            verify(observer).onChanged(CountersViewModel.UiModel.Loading)
            verify(observer).onChanged(CountersViewModel.UiModel.ShowCountersError(expected))
        }
    }

    @Test
    fun `delete selected counters counter value with success`() {
        runBlocking {
            // given
            setFieldHelpers()
            val expected = listOf(counters[0])
            viewModel.model.observeForever(observer)
            whenever(mockInteractor.deleteCounter(any())).thenReturn(SuccessResponse(expected))

            // when
            viewModel.deleteSelectedCounters()

            verify(observer).onChanged(CountersViewModel.UiModel.ShowCounters(expected))
            verify(observer).onChanged(CountersViewModel.UiModel.ShowSelectedCounters(listOf()))
        }
    }

    @Test
    fun `delete selected counters counter value with error`() {
        runBlocking {
            // given
            setFieldHelpers()
            val expected = NetworkError
            viewModel.model.observeForever(observer)
            whenever(mockInteractor.deleteCounter(any())).thenReturn(ErrorResponse(expected))

            // when
            viewModel.deleteSelectedCounters()

            verify(observer).onChanged(CountersViewModel.UiModel.DeleteError(expected))
        }
    }

    @Test
    fun `filter by query counters return all items`() {
        runBlocking {
            // given
            setFieldHelpers()
            val expected = counters
            viewModel.model.observeForever(observer)

            // when
            viewModel.filterByQuery("tit")

            verify(observer).onChanged(CountersViewModel.UiModel.ShowCounters(expected))
        }
    }

    @Test
    fun `filter by query counters return one item`() {
        runBlocking {
            // given
            setFieldHelpers()
            val expected = listOf(counters[0])
            viewModel.model.observeForever(observer)

            // when
            viewModel.filterByQuery("title1")

            verify(observer).onChanged(CountersViewModel.UiModel.ShowCounters(expected))
        }
    }

    @Test
    fun `filter by query counters return no item`() {
        runBlocking {
            // given
            setFieldHelpers()
            viewModel.model.observeForever(observer)

            // when
            viewModel.filterByQuery("title3")

            verify(observer).onChanged(CountersViewModel.UiModel.NoCountersError)
        }
    }

    private fun setFieldHelpers() {
        setFieldHelper(
            FIELD_COUNTERS,
            counters,
            viewModel
        )
    }
}