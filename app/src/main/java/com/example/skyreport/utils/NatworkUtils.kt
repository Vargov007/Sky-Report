package com.example.skyreport.utils

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NatworkUtils {
    private const val Base_Url = "https://api.openweathermap.org/data/2.5/"

    fun getRetrofitInstance(): Retrofit{
        return Retrofit.Builder()
            .baseUrl(Base_Url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}