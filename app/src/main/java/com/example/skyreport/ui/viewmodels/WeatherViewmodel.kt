package com.example.skyreport.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skyreport.data.models.weather.WeatherResponce
import com.example.skyreport.data.repo.WeatherRepo
import com.example.skyreport.utils.Result
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn

class WeatherViewmodel(private val repository: WeatherRepo): ViewModel()  {



    private val _cityname = MutableStateFlow("Kamakhyaguri")
    val cityName : StateFlow<String> = _cityname

    // Automatically reacts to cityName changes and fetches the weather data
    @OptIn(ExperimentalCoroutinesApi::class)
    val weatherState : StateFlow<Result<WeatherResponce>> = _cityname
        .flatMapLatest {city ->
            repository.getWeather(city)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Result.Loading
        )

    // Call this from your Activity/Fragment when the device location is found
    fun updateCity(newCity: String){
        _cityname.value = newCity
    }
    }

}