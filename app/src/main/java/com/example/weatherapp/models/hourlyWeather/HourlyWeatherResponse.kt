package com.example.weatherapp.models.hourlyWeather

data class HourlyWeatherResponse(
    val city: City,
    val cnt: Int,
    val cod: String,
    val list: List<Any>,
    val message: Double
)