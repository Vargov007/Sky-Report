package com.example.skyreport.data.models.hourlyweather

data class DisplayDateTime(
    val day: Int,
    val hours: Int,
    val minutes: Int,
    val month: Int,
    val nanos: Int,
    val seconds: Int,
    val utcOffset: String,
    val year: Int
)