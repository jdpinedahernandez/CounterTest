package com.cornershop.counterstest.counter.data

import com.cornershop.counterstest.counter.model.Counter
import com.cornershop.counterstest.utils.data.DataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TheCountersDbDataStore(private val dataStore: DataStore) : LocalDataSource {
    override suspend fun updateCounters(counters: List<Counter>) =
        withContext(Dispatchers.IO) { dataStore.put(COUNTERS_KEY, counters) }

    override suspend fun getCounters() =
        withContext(Dispatchers.IO) { dataStore.get<List<Counter>>(COUNTERS_KEY, listOf()) }
}