package com.example.skyreport.utils

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object HourlyNetworkUtils {

    private const val BaseURL = "https://weather.googleapis.com/v1/"

    fun getGoogleWeatherRetrofitInstance(): Retrofit{
       return Retrofit.Builder()
            .baseUrl(BaseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}