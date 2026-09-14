package com.example.skyreport.data.models.hourlyweather

data class HourlyWeatherResposce(
    val forecastHours: List<ForecastHour>,
    val nextPageToken: String,
    val timeZone: TimeZone
)