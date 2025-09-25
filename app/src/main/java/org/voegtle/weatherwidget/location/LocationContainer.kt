package org.voegtle.weatherwidget.location

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.platform.ComposeView
import org.voegtle.weatherwidget.data.Statistics
import org.voegtle.weatherwidget.data.WeatherData
import org.voegtle.weatherwidget.preferences.OrderCriteria
import org.voegtle.weatherwidget.state.StateCache
import org.voegtle.weatherwidget.util.DataFormatter

data class LocationDataSet(val weatherLocation: WeatherLocation, var caption: String, val weatherData: WeatherData, val statistics: Statistics?)

class LocationContainer(val context: Context, private val container: ComposeView) {
    private val stateCache = StateCache(context)
    private val locationOrderStore = LocationOrderStore(context)
    private val locationSorter = LocationSorter(context)
    private val formatter = DataFormatter()

    fun showWeatherData(locations: List<WeatherLocation>, data: Map<LocationIdentifier, WeatherData>,
                        onDiagramClick: (locationIdentifier: LocationIdentifier) -> Unit = {},
                        onForecastClick: (forecastUrl: Uri) -> Unit = {}) {
        val locationDataSets = assembleLocationDataSets(locations, data)
        enrichCaptionWithTimeAndDistance(locationDataSets)
        locationSorter.sort(locationDataSets)

        container.setContent {
            LazyColumn() {
                items(items = locationDataSets) { dataSet ->
                    LocationComposable(dataSet.caption, dataSet.weatherData, dataSet.statistics,
                        onDiagramClick = onDiagramClick, onForecastClick = onForecastClick)
                }
            }

        }

    }

    private fun enrichCaptionWithTimeAndDistance(locationDataSets: List<LocationDataSet>) {
        locationDataSets.forEach {
            it.caption = appendTimeAndDistance(it.weatherLocation.name, it.weatherData)
        }
    }

    private fun appendTimeAndDistance(locationName: String, data: WeatherData): String {
        var caption = "$locationName - ${data.localtime}"

        if (locationOrderStore.readOrderCriteria() == OrderCriteria.location) {
            val userPosition = locationOrderStore.readPosition()
            val distance = userPosition.distanceTo(data.position)
            caption += " - ${formatter.formatDistance(distance.toFloat())}"
        }

        return caption
    }
}

fun assembleLocationDataSets(
    locations: List<WeatherLocation>,
    data: Map<LocationIdentifier, WeatherData>
): List<LocationDataSet> {
    val locationDataSets = ArrayList<LocationDataSet>()

    for (location in locations) {
        val weatherData = data[location.key]
        if (weatherData != null) {
            locationDataSets.add(LocationDataSet(location, location.name, weatherData, null))
        }
    }
    return locationDataSets
}

