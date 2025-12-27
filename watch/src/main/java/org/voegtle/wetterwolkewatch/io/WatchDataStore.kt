package org.voegtle.wetterwolkewatch.io

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.voegtle.weatherwidget.data.LocationDataSet
import java.io.File

const val WEATHER_DATA_FILE = "weather-data.json"

class WatchDataStore(val context: Context) {
    private val gson = Gson()
    private val TAG = this::class.simpleName

    fun readDataFromFile(): List<LocationDataSet> {
        val file = File(context.filesDir, WEATHER_DATA_FILE)
        return if (file.exists()) {
            try {
                val json = file.readText()
                val listType = object : TypeToken<List<LocationDataSet>>() {}.type
                gson.fromJson(json, listType)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to deserialize weather data", e)
                emptyList()
            }
        } else {
            emptyList()
        }
    }

    fun writeData(jsonByteArray: ByteArray) {
        val json = jsonByteArray.toString(Charsets.UTF_8)
        val file = File(context.filesDir, WEATHER_DATA_FILE)
        file.writeText(json)
    }

}