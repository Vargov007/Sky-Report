package com.example.skyreport.data.repo

import com.example.skyreport.data.api.WeatherApi
import com.example.skyreport.data.models.weather.WeatherResponce
import com.example.skyreport.utils.Resources
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class WeatherRepo(private val weatherApi: WeatherApi) {
    fun getWeather(cityName: String) : Flow<Resources<WeatherResponce>> = flow {
        emit(Resources.Loading())
        try {
            val responce = weatherApi.getWeather(cityName)
            emit(Resources.Success(responce))
        }catch (e:Exception){
            emit(Resources.Error(e.message?:"An error occurred"))
        }
    }

    suspend fun getWeatherData(cityName: String): WeatherResponce{
        val apiResponse : WeatherResponce = weatherApi.getWeather(cityName)
        return  apiResponse
    }
}