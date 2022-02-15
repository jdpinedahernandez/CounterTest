package com.cornershop.counterstest.counter.data

import com.cornershop.counterstest.counter.model.Counter

fun List<CounterRaw>.mapToModel(): List<Counter> = map { counter ->
    Counter(
        id = counter.id.orEmpty(),
        title = counter.title.orEmpty(),
        count = counter.count ?: 0
    )
}.filter { counter ->
    counter.id.isNotBlank() && counter.title.isNotBlank()
}