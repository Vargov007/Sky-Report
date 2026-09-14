package com.example.skyreport.data.models.hourlyweather

data class WeatherCondition(
    val description: Description,
    val iconBaseUri: String,
    val type: String
)