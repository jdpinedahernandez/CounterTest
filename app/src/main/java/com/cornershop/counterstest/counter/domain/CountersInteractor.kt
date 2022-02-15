package com.cornershop.counterstest.counter.domain

import com.cornershop.counterstest.counter.data.CounterService
import com.cornershop.counterstest.counter.model.Counter
import com.cornershop.counterstest.utils.data.ErrorResponse
import com.cornershop.counterstest.utils.data.ResultHandler

private const val MIN_TITLE_SIZE = 2

class CountersInteractor(private val service: CounterService) {

    suspend fun getCounters(): ResultHandler<List<Counter>> = service.getCounters()

    suspend fun addCounter(title: String): ResultHandler<List<Counter>> = when {
        title.isBlank() -> ErrorResponse(CounterBusiness.InvalidEmptyTitle)
        title.length < MIN_TITLE_SIZE -> ErrorResponse(CounterBusiness.InvalidTitleSize)
        else -> service.addCounter(title)
    }

    suspend fun deleteCounter(id: String): ResultHandler<List<Counter>> =
        if (id.isNotBlank()) service.deleteCounter(id)
        else ErrorResponse(CounterBusiness.InvalidId)

    suspend fun incrementCounter(id: String): ResultHandler<List<Counter>> =
        if (id.isNotBlank()) service.incrementCounter(id)
        else ErrorResponse(CounterBusiness.InvalidId)

    suspend fun decrementCounter(id: String): ResultHandler<List<Counter>> =
        if (id.isNotBlank()) service.decrementCounter(id)
        else ErrorResponse(CounterBusiness.InvalidId)
}