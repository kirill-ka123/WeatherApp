package com.example.weatherapp.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.weatherapp.MainActivity
import com.example.weatherapp.R
import com.example.weatherapp.ui.WeatherViewModel
import com.example.weatherapp.util.Constants
import com.example.weatherapp.util.Resource
import kotlinx.android.synthetic.main.current_weather_fragment.*
import kotlinx.android.synthetic.main.hourly_weather_fragment.*

class HourlyWeatherFragment: Fragment(R.layout.hourly_weather_fragment) {

    lateinit var viewModel: WeatherViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as MainActivity).viewModel

        viewModel.selectedLocation?.observe(viewLifecycleOwner, Observer { location ->
            var generatedLocation = ""

            if (location.local_names?.ru != null) {
                generatedLocation += location.local_names.ru
            } else if (location.local_names?.en != null) {
                generatedLocation += location.local_names.en
            } else if (location.name != null) {
                generatedLocation += location.name
            }

            if (location.country != null) {
                generatedLocation += ", " + location.country
            }
            name_of_location_in_hourly_fragment.text = generatedLocation

            viewModel.getHourlyWeather(location.lat, location.lon)

        })

        viewModel.currentWeather.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    response.data?.let { hourlyWeather ->

                    }
                }
                is Resource.Error -> {
                    response.message?.let { message ->
                        Toast.makeText(activity, "Произошла ошибка: $message", Toast.LENGTH_LONG).show()
                    }
                }
                is Resource.Loading -> {
                }
            }
        } )
    }
}