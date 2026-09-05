package com.example.skyreport.data.api

import com.example.skyreport.BuildConfig
import com.example.skyreport.data.models.weather.WeatherResponce
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("weather")
    suspend fun getWeather(
        @Query("q") cityName: String,
        @Query("appid") apiKey: String = BuildConfig.API_KEY,
        @Query("units") unit: String = "metric"
    ): WeatherResponce
}