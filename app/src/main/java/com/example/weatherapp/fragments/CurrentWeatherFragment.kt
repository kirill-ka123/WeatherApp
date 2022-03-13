package com.example.weatherapp.fragments

import android.content.pm.PackageManager
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.weatherapp.MainActivity
import com.example.weatherapp.R
import com.example.weatherapp.ui.WeatherViewModel
import com.example.weatherapp.util.Constants.Companion.PACKAGE_NAME
import com.example.weatherapp.util.Resource
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.current_weather_fragment.*


class CurrentWeatherFragment: Fragment(R.layout.current_weather_fragment) {
    lateinit var viewModel: WeatherViewModel
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as MainActivity).viewModel

        name_of_location.setOnClickListener {
            findNavController().navigate(R.id.action_currentWeatherFragment_to_savedLocationsFragment)
        }

        down_arrow_of_location.setOnClickListener {
            findNavController().navigate(R.id.action_currentWeatherFragment_to_savedLocationsFragment)
        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity as MainActivity)
        if (viewModel.selectedLocation == null) {
            isLocationPermissionGranted()
        }

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
            name_of_location.text = generatedLocation

            viewModel.getCurrentWeather(location.lat, location.lon)

        })

        viewModel.currentWeather.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    response.data?.let { weather ->
                        layout_temp_current_weather.visibility = View.VISIBLE
                        layout_detailed_information.visibility = View.VISIBLE
                        preview.visibility = View.INVISIBLE

                        if (name_of_location.text == "Местоположение") {
                            name_of_location.text = weather.name
                        }

                        current_temp.text = weather.main.temp.toInt().toString()

                        val mDrawableName = "ic_" + weather.weather[0].icon
                        val resID =
                            resources.getIdentifier(mDrawableName, "drawable",
                                PACKAGE_NAME
                            )
                        icon.setImageResource(resID)

                        weather_description.text = weather.weather[0].description

                        max_temp.text = resources.getString(R.string.max_temp, weather.main.temp_max.toString())
                        min_temp.text = resources.getString(R.string.min_temp, weather.main.temp_min.toString())
                        atmospheric_pressure.text = resources.getString(R.string.atmospheric_pressure, weather.main.pressure.toString())
                        humidity.text = resources.getString(R.string.humidity, weather.main.humidity.toString() + " %")
                        wind.text = resources.getString(R.string.wind, weather.main.temp_min.toString())
                        cloud_cover.text = resources.getString(R.string.cloud_cover, weather.wind.speed.toString() + " %")
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 101) {
            if (grantResults.contains(PackageManager.PERMISSION_GRANTED)) {
                isLocationPermissionGranted()
            } else {
                layout_temp_current_weather.visibility = View.INVISIBLE
                layout_detailed_information.visibility = View.INVISIBLE
                preview.visibility = View.VISIBLE
            }
        }
    }

    private fun isLocationPermissionGranted() {
        val task = fusedLocationProviderClient.lastLocation
        if (ActivityCompat.checkSelfPermission(
                activity as MainActivity,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                activity as MainActivity,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION),
                101
            )
        } else {
            task.addOnSuccessListener {
                if (it != null) {
                    viewModel.getCurrentWeather(it.latitude, it.longitude)
                }
            }
        }
    }
}