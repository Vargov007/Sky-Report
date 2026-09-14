package com.example.skyreport.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.skyreport.data.repo.HourlyweatherRepo

class HourlyWeatherViewModelFactory(private val hourlyWeatherRepo: HourlyweatherRepo) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HourlyWeatherViewmodel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HourlyWeatherViewmodel(hourlyWeatherRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}