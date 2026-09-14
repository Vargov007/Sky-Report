package com.example.skyreport.data.models.hourlyweather

data class Precipitation(
    val probability: Probability,
    val qpf: Qpf,
    val snowQpf: SnowQpf
)