package com.example.skyreport.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skyreport.data.models.weather.WeatherResponce
import com.example.skyreport.data.repo.WeatherRepo
import com.example.skyreport.utils.Resources
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WeatherViewmodel(
    private val repository: WeatherRepo
) : ViewModel() {

    private val _cityName = MutableStateFlow("_ _ _ _ _ _ _ _ _ _")

    private val _refreshTrigger = MutableSharedFlow<Unit>(replay = 1).apply {
        tryEmit(Unit) // Trigger initial load
    }

    fun refreshData(){
       viewModelScope.launch { _refreshTrigger.emit(Unit) }
    }

    // Automatically reacts to _cityName changes and updates state
    @OptIn(ExperimentalCoroutinesApi::class)
    val weatherState: StateFlow<Resources<WeatherResponce>> = combine(_cityName,_refreshTrigger){ city, _ -> city}
        .filter { it.isNotBlank() && it != "_ _ _ _ _ _ _ _ _ _" }
        .flatMapLatest { city ->
            repository.getWeather(city)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Resources.Loading()
        )

    // Call this from your Activity/Fragment/UI whenever the city changes or location is found
    fun updateCity(newCity: String) {
        _cityName.value = newCity
    }
}


