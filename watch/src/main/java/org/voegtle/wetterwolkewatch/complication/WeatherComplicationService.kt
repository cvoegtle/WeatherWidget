package org.voegtle.wetterwolkewatch.complication

import android.app.PendingIntent
import android.content.Intent
import androidx.wear.watchface.complications.data.ComplicationData
import androidx.wear.watchface.complications.data.ComplicationType
import androidx.wear.watchface.complications.data.PlainComplicationText
import androidx.wear.watchface.complications.data.ShortTextComplicationData
import androidx.wear.watchface.complications.datasource.ComplicationRequest
import androidx.wear.watchface.complications.datasource.SuspendingComplicationDataSourceService
import org.voegtle.weatherwidget.data.LocationDataSet
import org.voegtle.weatherwidget.util.DataFormatter
import org.voegtle.wetterwolkewatch.R
import org.voegtle.wetterwolkewatch.io.WatchDataStore
import org.voegtle.wetterwolkewatch.presentation.WeatherWatchActivity

/**
 * Skeleton for complication data source that returns short text.
 */
class WeatherComplicationService : SuspendingComplicationDataSourceService() {

    override fun getPreviewData(type: ComplicationType): ComplicationData? {
        if (type != ComplicationType.SHORT_TEXT) {
            return null
        }
        return createComplicationData("-", this.getString(R.string.waiting_for_data))
    }

    override suspend fun onComplicationRequest(request: ComplicationRequest): ComplicationData {
        val locationDataSet = WatchDataStore(this).readDataFromFile().firstOrNull()
        return createComplicationData(locationDataSet)

        }

    private fun createComplicationData(locationDataSet: LocationDataSet?): ComplicationData {
        return if (locationDataSet == null) {
            createComplicationData("-", this.getString(R.string.waiting_for_data))
        } else {
            val formattedTemperatureShort: String = DataFormatter().formatTemperatureShort(locationDataSet.weatherData.temperature)
            val formattedTemperature: String = DataFormatter().formatTemperature(locationDataSet.weatherData.temperature)
            createComplicationData(formattedTemperatureShort, formattedTemperature)
        }
    }

    private fun createComplicationData(text: String, contentDescription: String) =
        ShortTextComplicationData.Builder(
            text = PlainComplicationText.Builder(text).build(),
            contentDescription = PlainComplicationText.Builder(contentDescription).build()
        )
            .setTapAction(buildTapAction())
            .build()

    private fun buildTapAction(): PendingIntent {
        val intent = Intent(this, WeatherWatchActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        return PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}
