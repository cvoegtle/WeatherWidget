package org.voegtle.weatherwidget.cache

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import org.voegtle.weatherwidget.util.FetchAllResponse
import androidx.core.content.edit

class WeatherDataCache(context: Context) {
    private val WEATHERDATA_CACHE = "WEATHERDATA"
    private val LATEST = "LATEST"

    private val weatherDataPreferences: SharedPreferences = context.getSharedPreferences(WEATHERDATA_CACHE, 0)

    fun read(): FetchAllResponse? {
        val weatherDataJson = weatherDataPreferences.getString(LATEST, null)
        return if (weatherDataJson == null) {
            null
        } else {
            Gson().fromJson(weatherDataJson, FetchAllResponse::class.java)
        }
    }

    fun write(weatherData: String) {
        weatherDataPreferences.edit {
            putString(LATEST, weatherData)
        }
    }


}