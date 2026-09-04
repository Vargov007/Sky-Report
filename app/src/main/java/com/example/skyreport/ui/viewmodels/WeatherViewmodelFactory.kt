package com.example.skyreport.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.skyreport.data.repo.WeatherRepo

class WeatherViewmodelFactory(private val repository : WeatherRepo): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WeatherViewmodel::class.java)) {
            return WeatherViewmodel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}