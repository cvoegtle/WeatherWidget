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
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataItem
import com.google.android.gms.wearable.Wearable
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.voegtle.weatherwidget.data.LocationDataSet
import org.voegtle.wetterwolkewatch.WeatherScreen

private const val WEATHER_DATA_PATH = "/weather-data"

@OptIn(ExperimentalPagerApi::class)
class WetterWatchActivity : ComponentActivity(), DataClient.OnDataChangedListener {

    private var locationDataSetList by mutableStateOf<List<LocationDataSet>>(emptyList())
    private val dataClient by lazy { Wearable.getDataClient(this) }
    private val gson = Gson()
    private val TAG = this::class.simpleName


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                if (locationDataSetList.isNotEmpty()) {
                    HorizontalPager(count = locationDataSetList.size) { page ->
                        WeatherScreen(weatherData = locationDataSetList[page].weatherData)
                    }
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
            dataItem.data?.let {
                val json = it.toString(Charsets.UTF_8)
                val listType = object : TypeToken<List<LocationDataSet>>() {}.type
                locationDataSetList = gson.fromJson(json, listType)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to deserialize weather data", e)
        }
    }
}