package com.example.weatherapp.models.geocoding

import android.content.Context
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.weatherapp.db.Converters
import java.io.*

@Entity(tableName = "locations")
@TypeConverters(Converters::class)
data class LocationResponseItem(
    @PrimaryKey
    var id: Int?,
    val country: String?,
    val lat: Double,
    val local_names: LocalNames?,
    val lon: Double,
    val name: String?,
    val state: String?,
    var addButtonStatus: Boolean?
): Serializable