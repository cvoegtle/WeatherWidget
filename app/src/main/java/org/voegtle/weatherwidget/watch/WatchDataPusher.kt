package org.voegtle.weatherwidget.watch

import android.content.Context
import android.util.Log
import com.google.android.gms.wearable.PutDataRequest
import com.google.android.gms.wearable.Wearable
import com.google.gson.Gson
import org.voegtle.weatherwidget.data.LocationDataSet
import org.voegtle.weatherwidget.data.Statistics
import org.voegtle.weatherwidget.data.WeatherData
import org.voegtle.weatherwidget.location.LocationDataSetFactory
import org.voegtle.weatherwidget.location.LocationIdentifier
import org.voegtle.weatherwidget.location.LocationSorter
import org.voegtle.weatherwidget.preferences.ApplicationPreferences

const val WEATHER_DATA_PATH = "/weather-data"
const val STATISTICS_DATA_PATH = "/statistics-data"

class WatchDataPusher(val context: Context, val configuration: ApplicationPreferences) {
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

        val putDataRequest = createRequest(WEATHER_DATA_PATH, locationDataSets)
        push(putDataRequest)
    }

    fun sendStatisticsData(statistics: Collection<Statistics>) {
        val nodeClient = Wearable.getNodeClient(context)
        nodeClient.connectedNodes.addOnSuccessListener { nodes ->
            if (nodes.isNotEmpty()) {
                pushStatisticsData(statistics)
            }
        }
    }

    private fun pushStatisticsData(statistics: Collection<Statistics>) {
        val putDataRequest = createRequest(STATISTICS_DATA_PATH, statistics)
        push(putDataRequest)
    }

    private fun createRequest(path: String, data: Any): PutDataRequest {
        val putDataRequest = PutDataRequest.create(path)
        val locationDataJson = Gson().toJson(data)
        putDataRequest.data = locationDataJson.toByteArray(Charsets.UTF_8)
        putDataRequest.setUrgent()
        return putDataRequest
    }

    private fun push(putDataRequest: PutDataRequest) {
        Wearable.getDataClient(context).putDataItem(putDataRequest).addOnSuccessListener {
            Log.d(this::class.simpleName, "Sent ${putDataRequest.uri} to Wear OS")
        }.addOnFailureListener { e ->
            Log.e(this::class.simpleName, "Failed to send ${putDataRequest.uri} to Wear OS", e)
        }
    }
}
