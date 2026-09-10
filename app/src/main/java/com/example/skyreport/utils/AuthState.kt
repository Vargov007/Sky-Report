package com.example.skyreport.utils

import com.example.skyreport.data.models.Users

sealed class AuthUiState{
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    data class Success (val user: Users?): AuthUiState()
    data class Error(val message : String): AuthUiState()
}

sealed class Result<out T>{
    object Loading : Result<Nothing>()
    data class Success<out T>(val data : T) : Result<T>()
    data class Error(val message : String) : Result<Nothing>()
}
