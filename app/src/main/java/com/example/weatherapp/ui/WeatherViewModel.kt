package com.example.weatherapp.ui

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.models.currentWeather.CurrentWeatherResponse
import com.example.weatherapp.models.geocoding.LocationResponse
import com.example.weatherapp.models.geocoding.LocationResponseItem
import com.example.weatherapp.models.hourlyWeather.HourlyWeatherResponse
import com.example.weatherapp.repository.WeatherRepository
import com.example.weatherapp.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class WeatherViewModel(
    app: Application,
    val weatherRepository: WeatherRepository
): AndroidViewModel(app) {

    val currentWeather: MutableLiveData<Resource<CurrentWeatherResponse>> = MutableLiveData()
    var currentWeatherResponse: CurrentWeatherResponse? = null

    val hourlyWeather: MutableLiveData<Resource<HourlyWeatherResponse>> = MutableLiveData()
    var hourlyWeatherResponse: HourlyWeatherResponse? = null

    var location: MutableLiveData<Resource<LocationResponse>> = MutableLiveData()
    var locationResponse: LocationResponse? = null
    var selectedLocation: MutableLiveData<LocationResponseItem>? = null

    fun getCurrentWeather(lat: Double, lon: Double) = viewModelScope.launch {
        currentWeather.postValue(Resource.Loading())

        try {
            if (hasInternetConnection()) {
                val response = weatherRepository.getCurrentWeather(lat, lon)
                currentWeather.postValue(handleCurrentWeatherResponse(response))
            } else {
                currentWeather.postValue(Resource.Error("No internet connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> currentWeather.postValue(Resource.Error("Network Failure"))
                else -> currentWeather.postValue(Resource.Error("Conversion Error"))
            }
        }
    }

    private fun handleCurrentWeatherResponse(response: Response<CurrentWeatherResponse>): Resource<CurrentWeatherResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                currentWeatherResponse = resultResponse
                return Resource.Success(currentWeatherResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    fun getHourlyWeather(lat: Double, lon: Double) = viewModelScope.launch {
        hourlyWeather.postValue(Resource.Loading())

        try {
            if (hasInternetConnection()) {
                val response = weatherRepository.getHourlyWeather(lat, lon)
                hourlyWeather.postValue(handleHourlyWeatherResponse(response))
            } else {
                hourlyWeather.postValue(Resource.Error("No internet connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> hourlyWeather.postValue(Resource.Error("Network Failure"))
                else -> hourlyWeather.postValue(Resource.Error("Conversion Error"))
            }
        }
    }

    private fun handleHourlyWeatherResponse(response: Response<HourlyWeatherResponse>): Resource<HourlyWeatherResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                hourlyWeatherResponse = resultResponse
                return Resource.Success(hourlyWeatherResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    fun getLocationUsingCity(city: String) = viewModelScope.launch {
        try {
            if (hasInternetConnection()) {
                val response = weatherRepository.getLocationUsingCity(city)
                location.postValue(handleLocationUsingCityResponse(response))
            } else {
                location.postValue(Resource.Error("No internet connection"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> location.postValue(Resource.Error("Network Failure"))
                else -> location.postValue(Resource.Error("Conversion Error"))
            }
        }
    }

    private suspend fun handleLocationUsingCityResponse(response: Response<LocationResponse>): Resource<LocationResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                resultResponse.forEach { location ->
                    location.addButtonStatus = checkByLatLon(location.lat, location.lon)

                    if (location.id == null) {
                        location.id = location.lat.hashCode()
                    }
                }

                locationResponse = resultResponse
                return Resource.Success(locationResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    fun saveLocation(location: LocationResponseItem) = viewModelScope.launch {
        weatherRepository.upsert(location)
    }

    fun getLocationsLive() = weatherRepository.getLocationsLive()

    fun getLocations() = weatherRepository.getLocations()

    suspend fun checkByLatLon(lat: Double, lon: Double) = weatherRepository.checkByLatLon(lat, lon)

    fun deleteLocation(location: LocationResponseItem) = viewModelScope.launch {
        weatherRepository.deleteLocation(location)
    }

    private fun hasInternetConnection(): Boolean {
        val connectivityManager = getApplication<WeatherApplication>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.activeNetworkInfo?. run {
                return when(type) {
                    ConnectivityManager.TYPE_WIFI -> true
                    ConnectivityManager.TYPE_MOBILE -> true
                    ConnectivityManager.TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
        return false
    }
}