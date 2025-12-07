package org.voegtle.wetterwolkewatch.tile

import android.content.Context
import android.util.Log
import androidx.wear.protolayout.ColorBuilders.argb
import androidx.wear.protolayout.LayoutElementBuilders
import androidx.wear.protolayout.ResourceBuilders
import androidx.wear.protolayout.TimelineBuilders
import androidx.wear.protolayout.material.Colors
import androidx.wear.protolayout.material.Text
import androidx.wear.protolayout.material.Typography
import androidx.wear.protolayout.material.layouts.PrimaryLayout
import androidx.wear.tiles.RequestBuilders
import androidx.wear.tiles.TileBuilders
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.tiles.SuspendingTileService
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.voegtle.weatherwidget.data.LocationDataSet
import org.voegtle.weatherwidget.util.DataFormatter
import org.voegtle.wetterwolkewatch.WEATHER_DATA_FILE
import java.io.File

private const val RESOURCES_VERSION = "0"

@OptIn(ExperimentalHorologistApi::class)
class WetterTileService : SuspendingTileService() {
    private val gson = Gson()
    private val TAG = this::class.simpleName

    override suspend fun resourcesRequest(
        requestParams: RequestBuilders.ResourcesRequest
    ) = resources(requestParams)

    override suspend fun tileRequest(
        requestParams: RequestBuilders.TileRequest
    ): TileBuilders.Tile {
        val locationDataSet = readDataFromFile().firstOrNull()
        return tile(this, requestParams, locationDataSet)
    }

    private fun readDataFromFile(): List<LocationDataSet> {
        val file = File(filesDir, WEATHER_DATA_FILE)
        return if (file.exists()) {
            try {
                val json = file.readText()
                val listType = object : TypeToken<List<LocationDataSet>>() {}.type
                gson.fromJson(json, listType)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to deserialize weather data", e)
                emptyList()
            }
        } else {
            emptyList()
        }
    }
}

private fun resources(
    requestParams: RequestBuilders.ResourcesRequest
): ResourceBuilders.Resources {
    return ResourceBuilders.Resources.Builder()
        .setVersion(RESOURCES_VERSION)
        .build()
}

private fun tile(
    context: Context,
    requestParams: RequestBuilders.TileRequest,
    locationDataSet: LocationDataSet?
): TileBuilders.Tile {
    val singleTileTimeline = TimelineBuilders.Timeline.Builder()
        .addTimelineEntry(
            TimelineBuilders.TimelineEntry.Builder()
                .setLayout(
                    LayoutElementBuilders.Layout.Builder()
                        .setRoot(weatherTileLayout(context, requestParams, locationDataSet))
                        .build()
                )
                .build()
        )
        .build()

    return TileBuilders.Tile.Builder()
        .setResourcesVersion(RESOURCES_VERSION)
        .setTileTimeline(singleTileTimeline)
        .build()
}

private fun weatherTileLayout(
    context: Context,
    requestParams: RequestBuilders.TileRequest,
    locationDataSet: LocationDataSet?
): LayoutElementBuilders.LayoutElement {
    return PrimaryLayout.Builder(requestParams.deviceConfiguration)
        .setResponsiveContentInsetEnabled(true)
        .setContent(
            if (locationDataSet != null) {
                val formatter = DataFormatter()
                val weatherData = locationDataSet.weatherData
                val temperature = weatherData.temperature
                LayoutElementBuilders.Column.Builder()
                    .addContent(
                        Text.Builder(context, locationDataSet.caption)
                            .setTypography(Typography.TYPOGRAPHY_CAPTION1)
                            .setColor(argb(Colors.DEFAULT.onSurface))
                            .build()
                    )
                    .addContent(
                        Text.Builder(context, "${formatter.formatTemperature(temperature)} / ${formatter.formatHumidity(weatherData.humidity)}")
                            .setTypography(Typography.TYPOGRAPHY_CAPTION1)
                            .setColor(argb(Colors.DEFAULT.onSurface))
                            .build()
                    )
                    .build()
            } else {
                Text.Builder(context, "Warte auf Daten...")
                    .setTypography(Typography.TYPOGRAPHY_CAPTION1)
                    .setColor(argb(Colors.DEFAULT.onSurface))
                    .build()
            }
        ).build()
}
