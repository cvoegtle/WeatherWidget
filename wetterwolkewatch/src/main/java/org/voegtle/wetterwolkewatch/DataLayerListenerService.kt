package org.voegtle.wetterwolkewatch

import android.util.Log
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.WearableListenerService
import com.google.gson.Gson
import org.voegtle.weatherwidget.data.WeatherData

private const val WEATHER_DATA_PATH = "/weather-data"

class DataLayerListenerService : WearableListenerService() {

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        Log.d("DataLayerListener", "onDataChanged: $dataEvents")
        for (event in dataEvents) {
            if (event.type == com.google.android.gms.wearable.DataEvent.TYPE_CHANGED &&
                event.dataItem.uri.path == WEATHER_DATA_PATH
            ) {
                val dataMap = DataMapItem.fromDataItem(event.dataItem).dataMap
                val gson = Gson()
                for (key in dataMap.keySet()) {
                    val weatherDataJson = dataMap.getString(key)
                    val weatherData = gson.fromJson(weatherDataJson, WeatherData::class.java)
                    // TODO: Process the received weather data
                    Log.d("DataLayerListener", "Received weather data for $key: $weatherData")
                }
            }
        }
    }
}
