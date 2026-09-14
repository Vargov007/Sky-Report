package com.example.skyreport.data.api

import com.example.skyreport.BuildConfig
import com.example.skyreport.data.models.hourlyweather.HourlyWeatherResposce
import retrofit2.http.GET
import retrofit2.http.Query

interface HourlyWeatherApi {

    @GET("forecast/hours:lookup")
    suspend fun getHourlyWeather(
        @Query("location.latitude") latitude: Double,
        @Query("location.longitude") longitude: Double,
        @Query("key") hourlyApiKey: String = BuildConfig.HOURLY_API_KEY,
        @Query("hours") hours: Int = 24
    ): HourlyWeatherResposce
}