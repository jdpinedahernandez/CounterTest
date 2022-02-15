package com.cornershop.counterstest.counters.data

import com.cornershop.counterstest.counter.data.CounterInfrastructure
import com.cornershop.counterstest.counter.data.LocalDataSource
import com.cornershop.counterstest.counter.data.RemoteDataSource
import com.cornershop.counterstest.counter.model.Counter
import com.cornershop.counterstest.utils.data.ErrorResponse
import com.cornershop.counterstest.utils.data.NetworkError
import com.cornershop.counterstest.utils.data.SuccessResponse
import com.cornershop.counterstest.utils.data.UnauthorizedError
import com.cornershop.counterstest.utils.data.onError
import com.cornershop.counterstest.utils.data.onSuccess
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class CountersInfrastructureUnitTest {

    @Mock
    lateinit var remoteDataSource: RemoteDataSource
    @Mock
    lateinit var localDataSource: LocalDataSource

    private lateinit var infrastructure: CounterInfrastructure

    @Before
    fun setUp() {
        infrastructure = CounterInfrastructure(
            remoteDataSource,
            localDataSource
        )
    }

    @Test
    fun `get counters counter value with success`() {
        runBlocking {
            // given
            val expected = listOf<Counter>()
            whenever(remoteDataSource.getCounters()).thenReturn(SuccessResponse(expected))

            // when
            infrastructure.getCounters().onSuccess {
                Assert.assertEquals(expected, it)
                verify(localDataSource).updateCounters(it)
            }
        }
    }

    @Test
    fun `get counters counter value with NetworkError`() {
        runBlocking {
            // given
            val expected = NetworkError
            val expectedList = listOf<Counter>()
            whenever(remoteDataSource.getCounters()).thenReturn(ErrorResponse(expected))
            whenever(localDataSource.getCounters()).thenReturn(expectedList)

            // when
            infrastructure.getCounters().onSuccess {
                Assert.assertEquals(expectedList, it)
            }
        }
    }

    @Test
    fun `get counters counter value with UnauthorizedError`() {
        runBlocking {
            // given
            val expected = UnauthorizedError
            whenever(remoteDataSource.getCounters()).thenReturn(ErrorResponse(expected))

            // when
            infrastructure.getCounters().onError {
                Assert.assertEquals(expected, failure)
            }
        }
    }

    @Test
    fun `decrement counter value with success`() {
        runBlocking {
            // given
            val expected = listOf<Counter>()
            val title = "title"
            whenever(remoteDataSource.addCounter(any())).thenReturn(SuccessResponse(expected))

            // when
            infrastructure.addCounter(title).onSuccess {
                Assert.assertEquals(expected, it)
                verify(localDataSource).updateCounters(it)
            }
        }
    }

    @Test
    fun `decrement counter value with error`() {
        runBlocking {
            // given
            val expected = NetworkError
            val title = "title"
            whenever(remoteDataSource.addCounter(any())).thenReturn(ErrorResponse(expected))

            // when
            infrastructure.addCounter(title).onError {
                Assert.assertEquals(expected, failure)
            }
        }
    }

    @Test
    fun `deleteCounter counter value with success`() {
        runBlocking {
            // given
            val expected = listOf<Counter>()
            val id = "id"
            whenever(remoteDataSource.deleteCounter(any())).thenReturn(SuccessResponse(expected))

            // when
            infrastructure.deleteCounter(id).onSuccess {
                Assert.assertEquals(expected, it)
                verify(localDataSource).updateCounters(it)
            }
        }
    }

    @Test
    fun `deleteCounter counter value with error`() {
        runBlocking {
            // given
            val expected = NetworkError
            val id = "id"
            whenever(remoteDataSource.deleteCounter(any())).thenReturn(ErrorResponse(expected))

            // when
            infrastructure.deleteCounter(id).onError {
                Assert.assertEquals(expected, failure)
            }
        }
    }

    @Test
    fun `incrementCounter counter value with success`() {
        runBlocking {
            // given
            val expected = listOf<Counter>()
            val id = "id"
            whenever(remoteDataSource.incrementCounter(any())).thenReturn(SuccessResponse(expected))

            // when
            infrastructure.incrementCounter(id).onSuccess {
                Assert.assertEquals(expected, it)
                verify(localDataSource).updateCounters(it)
            }
        }
    }

    @Test
    fun `incrementCounter counter value with error`() {
        runBlocking {
            // given
            val expected = NetworkError
            val id = "id"
            whenever(remoteDataSource.incrementCounter(any())).thenReturn(ErrorResponse(expected))

            // when
            infrastructure.incrementCounter(id).onError {
                Assert.assertEquals(expected, failure)
            }
        }
    }

    @Test
    fun `decrementCounter counter value with success`() {
        runBlocking {
            // given
            val expected = listOf<Counter>()
            val id = "id"
            whenever(remoteDataSource.decrementCounter(any())).thenReturn(SuccessResponse(expected))

            // when
            infrastructure.decrementCounter(id).onSuccess {
                Assert.assertEquals(expected, it)
                verify(localDataSource).updateCounters(it)
            }
        }
    }

    @Test
    fun `decrementCounter counter value with error`() {
        runBlocking {
            // given
            val expected = NetworkError
            val id = "id"
            whenever(remoteDataSource.decrementCounter(any())).thenReturn(ErrorResponse(expected))

            // when
            infrastructure.decrementCounter(id).onError {
                Assert.assertEquals(expected, failure)
            }
        }
    }
}