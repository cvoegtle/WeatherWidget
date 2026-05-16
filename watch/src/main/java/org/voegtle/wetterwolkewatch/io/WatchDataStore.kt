package org.voegtle.wetterwolkewatch.io

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.voegtle.weatherwidget.data.LocationDataSet
import org.voegtle.weatherwidget.data.Statistics
import java.io.File

const val WEATHER_DATA_FILE = "location-data.json"
const val STATISTICS_DATA_FILE = "statistics-data.json"

class WatchDataStore(val context: Context) {
    private val gson = Gson()
    private val TAG = this::class.simpleName

    fun readLocationDataSets(): List<LocationDataSet> {
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

    fun writeLocationDataSets(jsonByteArray: ByteArray) {
        val json = jsonByteArray.toString(Charsets.UTF_8)
        val file = File(context.filesDir, WEATHER_DATA_FILE)
        file.writeText(json)
    }

    fun readStatistics(): List<Statistics> {
        val file = File(context.filesDir, STATISTICS_DATA_FILE)
        return if (file.exists()) {
            try {
                val json = file.readText()
                val listType = object : TypeToken<List<Statistics>>() {}.type
                gson.fromJson(json, listType)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to deserialize statistics data", e)
                emptyList()
            }
        } else {
            emptyList()
        }
    }

    fun writeStatistics(jsonByteArray: ByteArray) {
        val json = jsonByteArray.toString(Charsets.UTF_8)
        val file = File(context.filesDir, STATISTICS_DATA_FILE)
        file.writeText(json)
    }

}
