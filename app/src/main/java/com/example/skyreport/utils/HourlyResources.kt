package com.example.skyreport.utils

sealed class HourlyResources<T>(
    val data:T? = null,
    val message: String? =null
) {
    class Loading<T> : HourlyResources<T>()
    class Success<T>(data:T) : HourlyResources<T>(data)
    class Error<T>(message: String?, data: T? = null): HourlyResources<T>(data, message)
}