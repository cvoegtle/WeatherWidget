package org.voegtle.wetterwolkewatch

import android.util.Log
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.WearableListenerService
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.voegtle.weatherwidget.data.LocationDataSet
import org.voegtle.weatherwidget.data.WeatherData

private const val WEATHER_DATA_PATH = "/weather-data"

class DataLayerListenerService : WearableListenerService() {

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        Log.d("DataLayerListener", "onDataChanged: $dataEvents")
        for (event in dataEvents) {
            val dataItem = event.dataItem
            if (event.type == com.google.android.gms.wearable.DataEvent.TYPE_CHANGED &&
                dataItem.uri.path == WEATHER_DATA_PATH
            ) {
                val data: ByteArray? = dataItem.data
                data?.let {
                    val json = it.toString(Charsets.UTF_8)
                    val listType = object : TypeToken<List<LocationDataSet>>() {}.type
                    val locationDataSets: List<LocationDataSet> = Gson().fromJson(json, listType)
                }
            }
        }
    }
}
