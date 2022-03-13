package com.example.weatherapp

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.weatherapp.db.WeatherDatabase
import com.example.weatherapp.models.geocoding.LocationResponseItem
import com.example.weatherapp.repository.WeatherRepository
import com.example.weatherapp.ui.WeatherViewModel
import com.example.weatherapp.ui.WeatherViewModelProviderFactory
import com.google.android.gms.location.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.current_weather_fragment.*
import java.io.FileInputStream
import java.io.IOException
import java.io.ObjectInputStream

class MainActivity : AppCompatActivity() {

    lateinit var viewModel: WeatherViewModel

    override fun onCreate(savedInstanceState: Bundle?) {

        val weatherRepository = WeatherRepository(WeatherDatabase(this))
        val viewModelProviderFactory = WeatherViewModelProviderFactory(application, weatherRepository)
        viewModel = ViewModelProvider(this, viewModelProviderFactory).get(WeatherViewModel::class.java)

        val locationFile = readFromFile(baseContext)
        if (locationFile != null) {
            Log.d("qwert", "not null")
            viewModel.selectedLocation = MutableLiveData(locationFile)
        }

        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        window.statusBarColor = Color.WHITE
        window!!.decorView.systemUiVisibility= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        setContentView(R.layout.activity_main)

        bottomNavigationView.setupWithNavController(WeatherNavHostFragment.findNavController())

    }

    fun readFromFile(context: Context): LocationResponseItem? {
        var location: LocationResponseItem? = null
        try {
            val fileInputStream: FileInputStream = context.openFileInput("fileName")
            val objectInputStream = ObjectInputStream(fileInputStream)
            location = objectInputStream.readObject() as LocationResponseItem
            objectInputStream.close()
            fileInputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        }
        return location
    }
}