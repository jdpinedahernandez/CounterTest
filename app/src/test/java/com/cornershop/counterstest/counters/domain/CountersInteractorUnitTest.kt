package com.cornershop.counterstest.counters.domain

import com.cornershop.counterstest.counter.data.CounterRequestAdd
import com.cornershop.counterstest.counter.data.CounterService
import com.cornershop.counterstest.counter.domain.CounterBusiness
import com.cornershop.counterstest.counter.domain.CountersInteractor
import com.cornershop.counterstest.counter.model.Counter
import com.cornershop.counterstest.counter.viewmodel.CountersViewModel
import com.cornershop.counterstest.utils.data.ErrorResponse
import com.cornershop.counterstest.utils.data.NetworkError
import com.cornershop.counterstest.utils.data.SuccessResponse
import com.cornershop.counterstest.utils.data.onError
import com.cornershop.counterstest.utils.data.onSuccess
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class CountersInteractorUnitTest {

    @Mock
    lateinit var mockInfrastructure: CounterService

    private lateinit var interactor: CountersInteractor

    @Before
    fun setUp() {
        interactor = CountersInteractor(mockInfrastructure)
    }

    @Test
    fun `get counters value with success with empty list`() {
        runBlocking {
            // given
            val expected = listOf<Counter>()
            whenever(mockInfrastructure.getCounters()).thenReturn(SuccessResponse(expected))

            // when
            interactor.getCounters().onSuccess {
                assertEquals(expected, it)
            }
        }
    }

    @Test
    fun `get counters value with error`() {
        runBlocking {
            // given
            val expected = NetworkError
            whenever(mockInfrastructure.getCounters()).thenReturn(ErrorResponse(expected))

            // when
            interactor.getCounters().onError {
                assertEquals(expected, failure)
            }
        }
    }

    @Test
    fun `add counter value with success`() {
        runBlocking {
            // given
            val expected = listOf<Counter>()
            val title = "title"
            whenever(mockInfrastructure.addCounter(title)).thenReturn(SuccessResponse(expected))

            // when
            interactor.addCounter(title).onSuccess {
                assertEquals(expected, it)
            }
        }
    }

    @Test
    fun `add counter value with error InvalidEmptyTitle`() {
        runBlocking {
            // given
            val expected = CounterBusiness.InvalidEmptyTitle
            val title = ""

            // when
            interactor.addCounter(title).onError {
                assertEquals(expected, failure)
            }
        }
    }

    @Test
    fun `add counter value with error InvalidTitleSize`() {
        runBlocking {
            // given
            val expected = CounterBusiness.InvalidTitleSize
            val title = "g"

            // when
            interactor.addCounter(title).onError {
                assertEquals(expected, failure)
            }
        }
    }

    @Test
    fun `delete counter value with success`() {
        runBlocking {
            // given
            val expected = listOf<Counter>()
            val id = "id"
            whenever(mockInfrastructure.deleteCounter(id)).thenReturn(SuccessResponse(expected))

            // when
            interactor.deleteCounter(id).onSuccess {
                assertEquals(expected, it)
            }
        }
    }

    @Test
    fun `delete counter value with error InvalidEmptyTitle`() {
        runBlocking {
            // given
            val expected = CounterBusiness.InvalidId
            val id = ""

            // when
            interactor.deleteCounter(id).onError {
                assertEquals(expected, failure)
            }
        }
    }

    @Test
    fun `increment counter value with success`() {
        runBlocking {
            // given
            val expected = listOf<Counter>()
            val id = "id"
            whenever(mockInfrastructure.incrementCounter(id)).thenReturn(SuccessResponse(expected))

            // when
            interactor.incrementCounter(id).onSuccess {
                assertEquals(expected, it)
            }
        }
    }

    @Test
    fun `increment counter value with error InvalidEmptyTitle`() {
        runBlocking {
            // given
            val expected = CounterBusiness.InvalidId
            val id = ""

            // when
            interactor.incrementCounter(id).onError {
                assertEquals(expected, failure)
            }
        }
    }

    @Test
    fun `decrement counter value with success`() {
        runBlocking {
            // given
            val expected = listOf<Counter>()
            val id = "id"
            whenever(mockInfrastructure.decrementCounter(id)).thenReturn(SuccessResponse(expected))

            // when
            interactor.decrementCounter(id).onSuccess {
                assertEquals(expected, it)
            }
        }
    }

    @Test
    fun `decrement counter value with error InvalidEmptyTitle`() {
        runBlocking {
            // given
            val expected = CounterBusiness.InvalidId
            val id = ""

            // when
            interactor.decrementCounter(id).onError {
                assertEquals(expected, failure)
            }
        }
    }
}