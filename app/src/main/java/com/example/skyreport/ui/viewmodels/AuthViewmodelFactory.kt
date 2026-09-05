package com.example.skyreport.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.skyreport.data.repo.AuthRepository
class AuthViewmodelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewmodel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthViewmodel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}