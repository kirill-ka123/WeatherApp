package com.example.weatherapp.models.weather

data class WeatherResponse(
    val current: Current?,
    val lat: Double?,
    val lon: Double?,
    val minutely: List<Minutely>?,
    val timezone: String?,
    val timezone_offset: Int?
)