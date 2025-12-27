package org.voegtle.wetterwolkewatch.tile

import android.content.Context
import androidx.wear.protolayout.ActionBuilders
import androidx.wear.protolayout.ColorBuilders.argb
import androidx.wear.protolayout.DimensionBuilders
import androidx.wear.protolayout.LayoutElementBuilders
import androidx.wear.protolayout.LayoutElementBuilders.HORIZONTAL_ALIGN_CENTER
import androidx.wear.protolayout.ModifiersBuilders
import androidx.wear.protolayout.ResourceBuilders
import androidx.wear.protolayout.TimelineBuilders
import androidx.wear.protolayout.material.Chip
import androidx.wear.protolayout.material.Colors
import androidx.wear.protolayout.material.CompactChip
import androidx.wear.protolayout.material.Text
import androidx.wear.protolayout.material.Typography
import androidx.wear.protolayout.material.layouts.PrimaryLayout
import androidx.wear.tiles.RequestBuilders
import androidx.wear.tiles.TileBuilders
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.tiles.SuspendingTileService
import org.voegtle.weatherwidget.data.LocationDataSet
import org.voegtle.weatherwidget.util.DataFormatter
import org.voegtle.wetterwolkewatch.R
import org.voegtle.wetterwolkewatch.io.AppMessenger
import org.voegtle.wetterwolkewatch.io.WatchDataStore

private const val RESOURCES_VERSION = "1"
private const val REFRESH_ACTION = "refresh"


@OptIn(ExperimentalHorologistApi::class)
class WeatherTileService : SuspendingTileService() {
    override suspend fun resourcesRequest(
        requestParams: RequestBuilders.ResourcesRequest
    ) = resources()

    override suspend fun tileRequest(
        requestParams: RequestBuilders.TileRequest
    ): TileBuilders.Tile {
        if (requestParams.currentState.lastClickableId == REFRESH_ACTION) {
            AppMessenger(this).requestDataUpdate()
        }
        val locationDataSet = WatchDataStore(this).readDataFromFile().firstOrNull()
        return tile(this, requestParams, locationDataSet)
    }


}

private fun resources(): ResourceBuilders.Resources {
    return ResourceBuilders.Resources.Builder()
        .setVersion(RESOURCES_VERSION)
        .addIdToImageMapping(
            "refresh_icon", ResourceBuilders.ImageResource.Builder()
                .setAndroidResourceByResId(
                    ResourceBuilders.AndroidImageResourceByResId.Builder()
                        .setResourceId(R.drawable.ic_refresh)
                        .build()
                )
                .build()
        )
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
    val refreshButton = CompactChip.Builder(context, context.getString(R.string.update), ModifiersBuilders.Clickable.Builder()
        .setId(REFRESH_ACTION)
        .setOnClick(ActionBuilders.LoadAction.Builder().build())
        .build(), requestParams.deviceConfiguration)
        .setIconContent("refresh_icon")
        .build()

    return PrimaryLayout.Builder(requestParams.deviceConfiguration)
        .setResponsiveContentInsetEnabled(true)
        .setContent(
            if (locationDataSet != null) {
                val formatter = DataFormatter()
                val weatherData = locationDataSet.weatherData
                val temperature = weatherData.temperature
                val rainFormatted = weatherData.rainToday?.let { rainToday -> formatter.formatRain(rainToday) + if (weatherData.rain != null) { " / " + formatter.formatRain(weatherData.rain) } else "" } ?: ""
                val barometerFormatted = weatherData.barometer?.let { " " + formatter.formatBarometer(it) } ?: ""
                val weatherFormatted = "${formatter.formatTemperature(temperature)} / ${formatter.formatHumidity(weatherData.humidity)}"
                LayoutElementBuilders.Column.Builder()
                    .addContent(
                        Text.Builder(context,
                            "${locationDataSet.weatherData.location_short} - ${weatherData.localtime}")
                            .setTypography(Typography.TYPOGRAPHY_CAPTION1)
                            .setColor(argb(Colors.DEFAULT.onSurface))
                            .build()
                    )
                    .addContent(
                        LayoutElementBuilders.Spacer.Builder()
                            .setHeight(DimensionBuilders.dp(10f))
                            .build()
                    )
                    .addContent(
                        Chip.Builder(context,
                            ModifiersBuilders.Clickable.Builder().build(),
                            requestParams.deviceConfiguration)
                            .setHorizontalAlignment(HORIZONTAL_ALIGN_CENTER)
                            .setPrimaryLabelContent( weatherFormatted)
                            .setSecondaryLabelContent(rainFormatted + barometerFormatted)
                            .build()
                    )
                    .build()
            } else {
                Chip.Builder(context, ModifiersBuilders.Clickable.Builder().build(), requestParams.deviceConfiguration)
                    .setHorizontalAlignment(HORIZONTAL_ALIGN_CENTER)
                    .setPrimaryLabelContent(context.getString(R.string.waiting_for_data))
                    .build()
            }
        ).setPrimaryChipContent(refreshButton).build()
}
