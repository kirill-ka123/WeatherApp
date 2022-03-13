package com.example.weatherapp.db

import androidx.room.TypeConverter
import com.example.weatherapp.models.geocoding.LocalNames

class Converters {

    @TypeConverter
    fun fromLocalNames(localName: LocalNames?): String {
        return localName?.ru + "," + localName?.en
    }

    @TypeConverter
    fun toLocalNames(country: String): LocalNames {
        val ruEn = country.split(",")
        if (ruEn[0] == "null" && ruEn[1] == "null") {
            return LocalNames(null, null)
        } else if (ruEn[0] == "null") {
            return LocalNames(null, ruEn[1])
        } else if (ruEn[1] == "null") {
            return LocalNames(ruEn[0], null)
        }
        return LocalNames(ruEn[0], ruEn[1])
    }
}