package com.example.weatherapp.models.currentWeather

data class Weather(
    val description: String,
    val icon: String,
    val id: Int,
    val main: String
)