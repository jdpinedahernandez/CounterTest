package com.cornershop.counterstest.welcome.data

interface WelcomeService {

    suspend fun isFirstTime(): Boolean
}