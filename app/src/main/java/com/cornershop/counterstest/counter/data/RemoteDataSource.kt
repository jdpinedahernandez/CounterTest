package com.cornershop.counterstest.counter.data

import com.cornershop.counterstest.counter.model.Counter
import com.cornershop.counterstest.utils.data.ResultHandler

interface RemoteDataSource {
    suspend fun getCounters(): ResultHandler<List<Counter>>
    suspend fun addCounter(title: String): ResultHandler<List<Counter>>
    suspend fun deleteCounter(id: String): ResultHandler<List<Counter>>
    suspend fun incrementCounter(id: String): ResultHandler<List<Counter>>
    suspend fun decrementCounter(id: String): ResultHandler<List<Counter>>
}