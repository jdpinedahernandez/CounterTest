package com.cornershop.counterstest.counter.data

import com.cornershop.counterstest.counter.model.Counter
import com.cornershop.counterstest.utils.data.DataStore
import com.cornershop.counterstest.utils.data.ErrorResponse
import com.cornershop.counterstest.utils.data.NetworkError
import com.cornershop.counterstest.utils.data.ResultHandler
import com.cornershop.counterstest.utils.data.SuccessResponse
import com.cornershop.counterstest.utils.data.onError
import com.cornershop.counterstest.utils.data.onSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

const val COUNTERS_KEY = "counter"

class CounterInfrastructure(
    private val remoteDataSource: RemoteDataSource,
    private val dataStore: DataStore
) : CounterService {

    override suspend fun getCounters(): ResultHandler<List<Counter>> {
        lateinit var result: ResultHandler<List<Counter>>
        remoteDataSource.getCounters()
            .onSuccess {
                updateLocalCounters(it)
                result = SuccessResponse(it)
            }.onError {
                val localData = getLocalCounters()
                result =
                    if (failure == NetworkError) SuccessResponse(localData)
                    else ErrorResponse(failure)
            }
        return result
    }

    override suspend fun addCounter(title: String): ResultHandler<List<Counter>> =
        remoteDataSource.addCounter(title).onSuccess { updateLocalCounters(it) }

    override suspend fun deleteCounter(id: String): ResultHandler<List<Counter>> =
        remoteDataSource.deleteCounter(id).onSuccess { updateLocalCounters(it) }

    override suspend fun incrementCounter(id: String): ResultHandler<List<Counter>> =
        remoteDataSource.incrementCounter(id).onSuccess { updateLocalCounters(it) }

    override suspend fun decrementCounter(id: String): ResultHandler<List<Counter>> =
        remoteDataSource.decrementCounter(id).onSuccess { updateLocalCounters(it) }

    private suspend fun updateLocalCounters(counters: List<Counter>) =
        withContext(Dispatchers.IO) { dataStore.put(COUNTERS_KEY, counters) }

    private suspend fun getLocalCounters() =
        withContext(Dispatchers.IO) { dataStore.get<List<Counter>>(COUNTERS_KEY, listOf()) }
}