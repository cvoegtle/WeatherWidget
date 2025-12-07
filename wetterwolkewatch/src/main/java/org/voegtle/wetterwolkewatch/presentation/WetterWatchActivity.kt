package org.voegtle.wetterwolkewatch.presentation

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.Text
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataItem
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.Wearable
import com.google.gson.Gson
import org.voegtle.weatherwidget.data.WeatherData
import org.voegtle.weatherwidget.location.LocationIdentifier
import org.voegtle.wetterwolkewatch.WeatherScreen

private const val WEATHER_DATA_PATH = "/weather-data"

class WetterWatchActivity : ComponentActivity(), DataClient.OnDataChangedListener {

    private var weatherData by mutableStateOf<WeatherData?>(null)
    private val dataClient by lazy { Wearable.getDataClient(this) }
    private val gson = Gson()
    private val TAG = this::class.simpleName


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                val data = weatherData
                if (data != null) {
                    WeatherScreen(weatherData = data)
                } else {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Warte auf Daten...")
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        dataClient.addListener(this)
        fetchData()
    }

    override fun onPause() {
        super.onPause()
        dataClient.removeListener(this)
    }

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        Log.d(TAG, "onDataChanged: $dataEvents")
        dataEvents
            .filter { it.type == DataEvent.TYPE_CHANGED && it.dataItem.uri.path == WEATHER_DATA_PATH }
            .forEach { event ->
                updateWeatherData(event.dataItem)
            }
    }

    private fun fetchData() {
        val uri = Uri.Builder()
            .scheme("wear")
            .authority("*")
            .path(WEATHER_DATA_PATH)
            .build()

        dataClient.getDataItems(uri).addOnSuccessListener { dataItemBuffer ->
            dataItemBuffer.forEach { dataItem ->
                updateWeatherData(dataItem)
            }
            dataItemBuffer.release()
        }.addOnFailureListener { e ->
            Log.e(TAG, "Failed to fetch data items", e)
        }
    }


    private fun updateWeatherData(dataItem: DataItem) {
        try {
            val dataMap = DataMapItem.fromDataItem(dataItem).dataMap
            var key = LocationIdentifier.Paderborn.id
            var json = dataMap.getString(key)

            if (json == null) {
                dataMap.keySet().firstOrNull()?.let { fallbackKey ->
                    key = fallbackKey
                    json = dataMap.getString(fallbackKey)
                    Log.d(TAG, "Paderborn not found, falling back to $fallbackKey")
                }
            }

            if (json != null) {
                weatherData = gson.fromJson(json, WeatherData::class.java)
                Log.d(TAG, "Updated weather data for $key: $weatherData")
            } else {
                Log.w(TAG, "No weather data found in DataItem.")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to deserialize weather data", e)
        }
    }
}
