package com.example.skyreport.data.models.hourlyweather

data class ForecastHour(
    val airPressure: AirPressure,
    val cloudCover: Int,
    val dewPoint: DewPoint,
    val displayDateTime: DisplayDateTime,
    val feelsLikeTemperature: FeelsLikeTemperature,
    val heatIndex: HeatIndex,
    val iceThickness: IceThickness,
    val interval: Interval,
    val isDaytime: Boolean,
    val precipitation: Precipitation,
    val relativeHumidity: Int,
    val temperature: Temperature,
    val thunderstormProbability: Int,
    val uvIndex: Int,
    val visibility: Visibility,
    val weatherCondition: WeatherCondition,
    val wetBulbTemperature: WetBulbTemperature,
    val wind: Wind,
    val windChill: WindChill
)