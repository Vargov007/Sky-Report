package com.example.skyreport.utils

import com.google.firebase.auth.FirebaseUser

sealed class AuthUiState{
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    data class Success (val user : FirebaseUser?): AuthUiState()
    data class Error(val message : String): AuthUiState()
}

sealed class Result<out T>{
    object Loading : Result<Nothing>()
    data class Success<out T>(val data : T) : Result<T>()
    data class Error(val message : String) : Result<Nothing>()
}
