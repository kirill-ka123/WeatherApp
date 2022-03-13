package com.example.weatherapp.api

import com.example.weatherapp.models.geocoding.LocationResponse
import com.example.weatherapp.models.currentWeather.CurrentWeatherResponse
import com.example.weatherapp.models.hourlyWeather.HourlyWeatherResponse
import com.example.weatherapp.util.Constants.Companion.API_KEY
import com.example.weatherapp.util.Constants.Companion.LANGUAGE
import com.example.weatherapp.util.Constants.Companion.TYPE_OF_TEMPERATURE
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherAPI {
    @GET("data/2.5/weather")
    suspend fun getCurrentWeather(
        @Query("lat")
        lat: Double,
        @Query("lon")
        lon: Double,
        @Query("appid")
        apiKey: String = API_KEY,
        @Query("units")
        units: String = TYPE_OF_TEMPERATURE,
        @Query("lang")
        language: String = LANGUAGE
    ): Response<CurrentWeatherResponse>

    @GET("geo/1.0/direct")
    suspend fun getLocationUsingCity(
        @Query("q")
        city: String?,
        @Query("appid")
        apiKey: String? = API_KEY,
        @Query("limit")
        limit: Int = 5
    ): Response<LocationResponse>

    @GET("data/2.5/forecast/hourly")
    suspend fun getHourlyWeather(
        @Query("lat")
        lat: Double,
        @Query("lon")
        lon: Double,
        @Query("appid")
        apiKey: String = API_KEY,
        @Query("lang")
        language: String = LANGUAGE
    ): Response<HourlyWeatherResponse>
}