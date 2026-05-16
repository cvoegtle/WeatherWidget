package org.voegtle.weatherwidget.watch

import android.content.Context
import android.util.Log
import com.google.android.gms.wearable.PutDataRequest
import com.google.android.gms.wearable.Wearable
import com.google.gson.Gson
import org.voegtle.weatherwidget.data.WeatherData
import org.voegtle.weatherwidget.location.LocationDataSetFactory
import org.voegtle.weatherwidget.location.LocationIdentifier
import org.voegtle.weatherwidget.location.LocationSorter
import org.voegtle.weatherwidget.preferences.ApplicationPreferences

const val WEATHER_DATA_PATH = "/weather-data"

class WatchDataStore(val context: Context, val configuration: ApplicationPreferences) {
    val locationDataSetFactory = LocationDataSetFactory(context)

    fun sendWeatherData(weatherMap: HashMap<LocationIdentifier, WeatherData>) {
        val nodeClient = Wearable.getNodeClient(context)
        nodeClient.connectedNodes.addOnSuccessListener { nodes ->
            if (nodes.isNotEmpty()) {
                pushWeatherData(weatherMap)
            }
        }
    }

    private fun pushWeatherData(weatherMap: HashMap<LocationIdentifier, WeatherData>) {
        val locationDataSets = locationDataSetFactory.assembleLocationDataSets(configuration.locations, weatherMap)
        LocationSorter(context).sort(locationDataSets)

        val putDataRequest = PutDataRequest.create(WEATHER_DATA_PATH)
        val locationDataJson = Gson().toJson(locationDataSets)
        putDataRequest.data = locationDataJson.toByteArray(Charsets.UTF_8)
        putDataRequest.setUrgent()
        Wearable.getDataClient(context).putDataItem(putDataRequest).addOnSuccessListener {
            Log.d(this::class.simpleName, "Sent weather data to Wear OS")
        }.addOnFailureListener { e ->
            Log.e(this::class.simpleName, "Failed to send weather data to Wear OS", e)
        }
    }
}