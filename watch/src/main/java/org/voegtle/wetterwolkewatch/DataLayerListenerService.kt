package org.voegtle.wetterwolkewatch

import android.content.Intent
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.wear.tiles.TileService
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.WearableListenerService
import org.voegtle.wetterwolkewatch.io.WatchDataStore
import org.voegtle.wetterwolkewatch.tile.WetterTileService
import java.io.File

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
                    val intent = Intent(ACTION_DATA_UPDATED)
                    LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
                    TileService.getUpdater(this).requestUpdate(WetterTileService::class.java)
                }
            }
        }
    }
}
