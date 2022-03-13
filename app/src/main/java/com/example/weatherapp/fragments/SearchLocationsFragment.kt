package com.example.weatherapp.fragments

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp.MainActivity
import com.example.weatherapp.R
import com.example.weatherapp.adapters.SearchLocationAdapter
import com.example.weatherapp.ui.WeatherViewModel
import com.example.weatherapp.util.Constants.Companion.SEARCH_LOCATIONS_TIME_DELAY
import com.example.weatherapp.util.Resource
import kotlinx.android.synthetic.main.search_locations_fragment.*
import kotlinx.coroutines.*

class SearchLocationsFragment: Fragment(R.layout.search_locations_fragment) {
    lateinit var viewModel: WeatherViewModel
    lateinit var searchLocationAdapter: SearchLocationAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as MainActivity).viewModel
        setupRecyclerView()
        focusOnEditText()
        viewModel.location = MutableLiveData()

        searchLocationAdapter.setOnItemClickListener { location ->
            if (location.addButtonStatus == false) {
                viewModel.saveLocation(location)
                location.addButtonStatus = true
            } else if (location.addButtonStatus == true) {
                viewModel.deleteLocation(location)
                location.addButtonStatus = false
            }
        }

        var job: Job? = null
        search_locations.addTextChangedListener { editable ->
            job?.cancel()
            job = MainScope().launch {
                delay(SEARCH_LOCATIONS_TIME_DELAY)
                editable?.let {
                    if (editable.toString().isNotEmpty()) {
                        viewModel.getLocationUsingCity(editable.toString())
                    }
                }
            }
        }

        back_to_saved_locations.setOnClickListener {
            findNavController().navigate(R.id.action_searchWeatherFragment_to_savedLocationsFragment)
        }

        viewModel.location.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    response.data?.let { locationsResponse ->
                        searchLocationAdapter.differ.submitList(locationsResponse.toList())
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

    private fun setupRecyclerView() {
        searchLocationAdapter = SearchLocationAdapter()
        rv_search_locations.apply {
            adapter = searchLocationAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }

    private fun focusOnEditText() {
        search_locations.isFocusableInTouchMode = true
        search_locations.isFocusable = true
        search_locations.requestFocus()
        val inputMethodManager = context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(search_locations, InputMethodManager.SHOW_IMPLICIT)
    }

    override fun onStart() {
        super.onStart()


    }

}