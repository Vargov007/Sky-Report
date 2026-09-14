package com.example.skyreport.data.repo

import com.example.skyreport.data.api.HourlyWeatherApi
import com.example.skyreport.data.models.hourlyweather.HourlyWeatherResposce
import com.example.skyreport.utils.HourlyResources
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class HourlyweatherRepo(private val hourlyWeatherApi: HourlyWeatherApi) {
    fun getHourlyWeather(latitude: Double, longitude: Double): Flow<HourlyResources<HourlyWeatherResposce>> =
        flow {
            emit(HourlyResources.Loading())
            try {
                val responce = hourlyWeatherApi.getHourlyWeather(latitude, longitude)
                emit(HourlyResources.Success(responce))
            }catch (e:Exception){
                emit(HourlyResources.Error(e.message?:"An error occurred"))
            }
        }
}