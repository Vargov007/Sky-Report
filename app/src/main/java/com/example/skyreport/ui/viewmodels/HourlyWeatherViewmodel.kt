package com.example.skyreport.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skyreport.data.models.hourlyweather.HourlyWeatherResposce
import com.example.skyreport.data.repo.HourlyweatherRepo
import com.example.skyreport.utils.HourlyResources
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn

data class LocationCoordinate(val lat : Double, val lon : Double)

class HourlyWeatherViewmodel(
    private val hourrepo: HourlyweatherRepo
) : ViewModel() {

    private val _coordination = MutableStateFlow(LocationCoordinate(0.0,0.0))

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val hourlyWeatherState: StateFlow<HourlyResources<HourlyWeatherResposce>> = _coordination
        .flatMapLatest { location ->
            hourrepo.getHourlyWeather(location.lat, location.lon)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = HourlyResources.Loading()
        )

    fun updateLocation(lat : Double, lon : Double){
        _coordination.value = LocationCoordinate(lat, lon)
    }

    fun refresh(){
        _coordination.value = _coordination.value.copy()
    }
}