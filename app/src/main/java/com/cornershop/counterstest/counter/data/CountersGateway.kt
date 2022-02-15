package com.cornershop.counterstest.counter.data

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.POST

interface CountersGateway {

    @GET("v1/counters")
    suspend fun getCounters(): List<CounterRaw>

    @POST("v1/counter")
    suspend fun addCounter(@Body request: CounterRequestAdd): List<CounterRaw>

    @HTTP(method = "DELETE", path = "v1/counter", hasBody = true)
    suspend fun deleteCounter(@Body request: CounterRequest): List<CounterRaw>

    @POST("v1/counter/inc")
    suspend fun incrementCounter(@Body request: CounterRequest): List<CounterRaw>

    @POST("v1/counter/dec")
    suspend fun decrementCounter(@Body request: CounterRequest): List<CounterRaw>

}