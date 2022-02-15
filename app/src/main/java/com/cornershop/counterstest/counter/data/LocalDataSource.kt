package com.cornershop.counterstest.counter.data

import com.cornershop.counterstest.counter.model.Counter

interface LocalDataSource {
    suspend fun updateCounters(counters: List<Counter>): Boolean
    suspend fun getCounters(): List<Counter>
}