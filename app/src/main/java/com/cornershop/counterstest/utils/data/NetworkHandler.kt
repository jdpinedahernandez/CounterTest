package com.cornershop.counterstest.utils.data

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

/**
 * Injectable class which handles device network connection.
 */
class NetworkHandler(private val context: Context) {

    private val Context.connectivityManager: ConnectivityManager
        get() = (this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager)

    val isConnected: Boolean
        get() {
            val connectivityManager = context.connectivityManager
            val network = connectivityManager.activeNetwork ?: return false
            val capabilities =
                connectivityManager.getNetworkCapabilities(network) ?: return false
            return when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        }
}

sealed class ResultHandler<out T : Any?>
class SuccessResponse<out T : Any?>(val data: T) : ResultHandler<T>()
class ErrorResponse(val failure: Throwable) : ResultHandler<Nothing>()

inline fun <T : Any> ResultHandler<T>.onSuccess(action: (T) -> Unit): ResultHandler<T> {
    if (this is SuccessResponse) action(data)
    return this
}

inline fun <T : Any> ResultHandler<T>.onError(action: ErrorResponse.() -> Unit): ResultHandler<T> {
    if (this is ErrorResponse) action(this)
    return this
}

suspend fun <T> NetworkHandler.resultHandlerOf(action: suspend () -> T) =
    when (isConnected) {
        false -> ErrorResponse(NetworkError)
        true -> runCatching { SuccessResponse(action()) }
            .getOrElse {
                it.printStackTrace()
                ErrorResponse(it)
            }
    }

