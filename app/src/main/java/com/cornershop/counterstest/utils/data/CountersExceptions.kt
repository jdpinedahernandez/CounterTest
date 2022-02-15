package com.cornershop.counterstest.utils.data

open class CountersException(message: String = "") : RuntimeException(message)

object NetworkError :
    CountersException("Device isn't connected, the Internet connection appears to be offline.")

object UnauthorizedError : CountersException()