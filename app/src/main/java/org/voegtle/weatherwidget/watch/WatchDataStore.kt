package org.voegtle.weatherwidget.watch

import android.content.Context
import android.util.Log
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import com.google.gson.Gson
import org.voegtle.weatherwidget.data.WeatherData
import org.voegtle.weatherwidget.location.LocationIdentifier

private const val WEATHER_DATA_PATH = "/weather-data"

class WatchDataStore(private val context: Context) {
    fun sendWeatherData(weatherMap: HashMap<LocationIdentifier, WeatherData>) {
        val nodeClient = Wearable.getNodeClient(context)
        nodeClient.connectedNodes.addOnSuccessListener { nodes ->
            if (nodes.isNotEmpty()) {
                pushWeatherData(weatherMap)
            }
        }
    }

    private fun pushWeatherData(weatherMap: HashMap<LocationIdentifier, WeatherData>) {
        weatherMap.forEach { weatherData ->
            val gson = Gson()
            val putDataMapReq = PutDataMapRequest.create(WEATHER_DATA_PATH)
            val weatherDataJson = gson.toJson(weatherData.value)
            putDataMapReq.dataMap.putString(weatherData.key.id, weatherDataJson)
            val putDataReq = putDataMapReq.asPutDataRequest().setUrgent()
            Wearable.getDataClient(context).putDataItem(putDataReq).addOnSuccessListener {
                Log.d(this::class.simpleName, "Sent weather data to Wear OS for ${weatherData.key.id}")
            }.addOnFailureListener { e ->
                Log.e(this::class.simpleName, "Failed to send weather data to Wear OS", e)
            }
        }
    }
}