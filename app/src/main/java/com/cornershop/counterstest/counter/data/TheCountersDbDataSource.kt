package com.cornershop.counterstest.counter.data

import com.cornershop.counterstest.counter.model.Counter
import com.cornershop.counterstest.utils.data.NetworkHandler
import com.cornershop.counterstest.utils.data.ResultHandler
import com.cornershop.counterstest.utils.data.resultHandlerOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TheCountersDbDataSource(
    private val theCountersDb: TheCountersDb,
    private val networkHandler: NetworkHandler
) : RemoteDataSource {
    override suspend fun getCounters(): ResultHandler<List<Counter>> =
        withContext(Dispatchers.IO) {
            networkHandler.resultHandlerOf {
                theCountersDb.service.getCounters().mapToModel()
            }
        }

    override suspend fun addCounter(title: String): ResultHandler<List<Counter>> =
        withContext(Dispatchers.IO) {
            networkHandler.resultHandlerOf {
                theCountersDb.service.addCounter(CounterRequestAdd(title)).mapToModel()
            }
        }

    override suspend fun deleteCounter(id: String): ResultHandler<List<Counter>> =
        withContext(Dispatchers.IO) {
            networkHandler.resultHandlerOf {
                theCountersDb.service.deleteCounter(CounterRequest(id)).mapToModel()
            }
        }

    override suspend fun incrementCounter(id: String): ResultHandler<List<Counter>> =
        withContext(Dispatchers.IO) {
            networkHandler.resultHandlerOf {
                theCountersDb.service.incrementCounter(CounterRequest(id)).mapToModel()
            }
        }

    override suspend fun decrementCounter(id: String): ResultHandler<List<Counter>> =
        withContext(Dispatchers.IO) {
            networkHandler.resultHandlerOf {
                theCountersDb.service.decrementCounter(CounterRequest(id)).mapToModel()
            }
        }
}