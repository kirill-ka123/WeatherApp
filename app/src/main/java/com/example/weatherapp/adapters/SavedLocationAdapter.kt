package com.example.weatherapp.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.models.geocoding.LocationResponseItem
import kotlinx.android.synthetic.main.item_saved_location_preview.view.*

class SavedLocationAdapter: RecyclerView.Adapter<SavedLocationAdapter.SavedLocationViewsHolder>() {

    inner class SavedLocationViewsHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    }

    private val differCallback = object : DiffUtil.ItemCallback<LocationResponseItem>() {
        override fun areItemsTheSame(oldItem: LocationResponseItem, newItem: LocationResponseItem): Boolean {
            return oldItem.lat == newItem.lat && oldItem.lon == newItem.lon
        }

        override fun areContentsTheSame(oldItem: LocationResponseItem, newItem: LocationResponseItem): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavedLocationViewsHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_saved_location_preview, parent, false)
        return SavedLocationViewsHolder(itemView)
    }

    override fun onBindViewHolder(holder: SavedLocationViewsHolder, position: Int) {
        val location = differ.currentList[position]
        holder.itemView.apply {
            location_in_rv_saved.text = generateLocation(location)

            delete_in_rv_saved.setOnClickListener {
                onItemClickListenerDelete?.let {
                    it(location)
                }
            }

            setOnClickListener {
                onItemClickListener?.let {
                    it(location)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private var onItemClickListener: ((LocationResponseItem) -> Unit)? = null

    fun setOnItemClickListener(listener: (LocationResponseItem) -> Unit) {
        onItemClickListener = listener
    }

    private var onItemClickListenerDelete: ((LocationResponseItem) -> Unit)? = null

    fun setOnItemClickListenerDelete(listener: (LocationResponseItem) -> Unit) {
        onItemClickListenerDelete = listener
    }

    private fun generateLocation(location: LocationResponseItem): String {
        var generatedLocation = ""

        if (location.local_names?.ru != null) {
            generatedLocation += location.local_names.ru
        } else if (location.local_names?.en != null) {
            generatedLocation += location.local_names.en
        } else if (location.name != null) {
            generatedLocation += location.name
        }
        if (location.state != null) {
            generatedLocation += ", " + location.state
        }
        if (location.country != null) {
            generatedLocation += ", " + location.country
        }
        return generatedLocation
    }
}