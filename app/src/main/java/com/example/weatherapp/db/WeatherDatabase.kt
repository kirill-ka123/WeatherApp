package com.example.weatherapp.db

import android.content.Context
import androidx.room.*
import com.example.weatherapp.models.geocoding.LocationResponseItem

@Database(
    entities = [LocationResponseItem::class],
    version = 1
)
abstract class WeatherDatabase: RoomDatabase() {
    abstract fun getWeatherDao(): WeatherDao

    companion object {
        @Volatile
        private var instance: WeatherDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: createDatabase(context).also { instance = it }
        }

        private fun createDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                WeatherDatabase::class.java,
                "weather_db.db"
            ).build()
    }
}