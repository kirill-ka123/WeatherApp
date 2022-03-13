package com.example.weatherapp.repository

import com.example.weatherapp.api.WeatherRetrofitInstance
import com.example.weatherapp.db.WeatherDatabase
import com.example.weatherapp.models.geocoding.LocationResponseItem

class WeatherRepository(val db: WeatherDatabase) {
    suspend fun getCurrentWeather(lat: Double, lon: Double) =
        WeatherRetrofitInstance.api.getCurrentWeather(
            lat,
            lon
        )

    suspend fun getHourlyWeather(lat: Double, lon: Double) =
        WeatherRetrofitInstance.api.getHourlyWeather(
            lat,
            lon
        )

    suspend fun getLocationUsingCity(city: String) =
        WeatherRetrofitInstance.api.getLocationUsingCity(city)

    suspend fun upsert(location: LocationResponseItem) =
        db.getWeatherDao().upsert(location)

    fun getLocationsLive() =
        db.getWeatherDao().getLocationsLive()

    fun getLocations() =
        db.getWeatherDao().getLocations()

    suspend fun checkByLatLon(lat: Double, lon: Double) =
        db.getWeatherDao().checkByLatLon(lat, lon)

    suspend fun deleteLocation(location: LocationResponseItem) =
        db.getWeatherDao().deleteLocation(location)

}