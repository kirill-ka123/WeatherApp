package com.example.weatherapp.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp.MainActivity
import com.example.weatherapp.R
import com.example.weatherapp.adapters.SavedLocationAdapter
import com.example.weatherapp.adapters.SearchLocationAdapter
import com.example.weatherapp.models.geocoding.LocationResponseItem
import com.example.weatherapp.ui.WeatherViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.saved_locations_fragment.*
import java.io.FileOutputStream
import java.io.IOException
import java.io.ObjectOutputStream

class SavedLocationsFragment: Fragment(R.layout.saved_locations_fragment) {

    lateinit var viewModel: WeatherViewModel
    lateinit var savedLocationAdapter: SavedLocationAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as MainActivity).viewModel
        setupRecyclerView()

        go_search_locations_in_search_fragment.setOnClickListener {
            findNavController().navigate(R.id.action_savedLocationsFragment_to_searchWeatherFragment)
        }

        back_to_current_weather.setOnClickListener {
            findNavController().navigate(R.id.action_savedLocationsFragment_to_currentWeatherFragment)
        }

        savedLocationAdapter.setOnItemClickListener { location ->
            viewModel.selectedLocation = MutableLiveData(location)
            saveToFile(activity as MainActivity, location)
            findNavController().navigate(R.id.action_savedLocationsFragment_to_currentWeatherFragment)
        }

        savedLocationAdapter.setOnItemClickListenerDelete { location ->
            viewModel.deleteLocation(location)
            Snackbar.make(view, "Местоположение успешно удалено", Snackbar.LENGTH_LONG).apply {
                setAction("Отмена") {
                    viewModel.saveLocation(location)
                }
                show()
            }
        }

        viewModel.getLocationsLive().observe(viewLifecycleOwner, Observer { locations ->
//            if (locations.isEmpty()) {
//                showStartTextOnSaveNews()
//            } else {
//                hideStartTextOnSaveNews()
//            }
            savedLocationAdapter.differ.submitList(locations)
        })

    }

    private fun setupRecyclerView() {
        savedLocationAdapter = SavedLocationAdapter()
        rv_saved_locations.apply {
            adapter = savedLocationAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }
    fun saveToFile(context: Context, location: LocationResponseItem) {
        try {
            val fileOutputStream: FileOutputStream =
                context.openFileOutput("fileName", Context.MODE_PRIVATE)
            val objectOutputStream = ObjectOutputStream(fileOutputStream)
            objectOutputStream.writeObject(location)
            objectOutputStream.close()
            fileOutputStream.close()
        } catch (e: IOException) {
            Log.d("qwert", "file not save")
            e.printStackTrace()
        }
    }
}