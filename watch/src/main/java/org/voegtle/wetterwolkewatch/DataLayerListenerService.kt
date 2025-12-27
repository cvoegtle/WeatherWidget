package org.voegtle.wetterwolkewatch

import android.content.ComponentName
import android.content.Intent
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.wear.tiles.TileService
import androidx.wear.watchface.complications.datasource.ComplicationDataSourceUpdateRequester
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.WearableListenerService
import org.voegtle.wetterwolkewatch.complication.WetterComplicationService
import org.voegtle.wetterwolkewatch.io.WatchDataStore
import org.voegtle.wetterwolkewatch.tile.WeatherTileService

private const val WEATHER_DATA_PATH = "/weather-data"
const val ACTION_DATA_UPDATED = "org.voegtle.wetterwolkewatch.DATA_UPDATED"


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
                    WatchDataStore(this).writeData(it)
                    informActivityAboutUpdatedData()
                    informTileAboutUpdatedData()
                    informComplicationAboutUpdatedData()
                }
            }
        }
    }

    private fun informActivityAboutUpdatedData() {
        val intent = Intent(ACTION_DATA_UPDATED)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    private fun informTileAboutUpdatedData() {
        TileService.getUpdater(this).requestUpdate(WeatherTileService::class.java)
    }

    private fun informComplicationAboutUpdatedData() {
        val complicationDataSourceUpdateRequester =
            ComplicationDataSourceUpdateRequester.create(
                context = this,
                complicationDataSourceComponent = ComponentName(
                    this,
                    WetterComplicationService::class.java
                )
            )
        complicationDataSourceUpdateRequester.requestUpdateAll()
    }


}
