package com.cornershop.counterstest.welcome.data

import com.cornershop.counterstest.utils.data.DataStore

private const val IS_FIRST_TIME_KEY = "is_first_time"

class WelcomeInfrastructure(private val dataStore: DataStore) : WelcomeService {

    override suspend fun isFirstTime() =
        (dataStore.get(IS_FIRST_TIME_KEY) ?: true).apply {
            if (this) {
                dataStore.put(IS_FIRST_TIME_KEY, false)
            }
        }
}