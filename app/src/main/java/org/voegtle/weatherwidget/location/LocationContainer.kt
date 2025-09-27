package org.voegtle.weatherwidget.location

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.platform.ComposeView
import org.voegtle.weatherwidget.data.Statistics
import org.voegtle.weatherwidget.data.WeatherData
import org.voegtle.weatherwidget.ui.theme.WeatherWidgetTheme

data class LocationDataSet(val weatherLocation: WeatherLocation, var caption: String, val weatherData: WeatherData, var statistics: Statistics?)

class LocationContainer(val context: Context, private val container: ComposeView) {

    fun showWeatherData(locationDataSets: List<LocationDataSet>,
                        onDiagramClick: (locationIdentifier: LocationIdentifier) -> Unit = {},
                        onForecastClick: (forecastUrl: Uri) -> Unit = {},
                        onExpandStateChanged: (locationIdentifier: LocationIdentifier, isExpanded: Boolean) -> Unit = { _, _ -> }) {

        container.setContent {
            WeatherWidgetTheme {
                LazyColumn() {
                    items(items = locationDataSets) { dataSet ->
                        LocationComposable(dataSet.caption, dataSet.weatherData, dataSet.statistics,
                            onDiagramClick = onDiagramClick, onForecastClick = onForecastClick, onExpandStateChanged = onExpandStateChanged)
                    }
                }
            }
        }

    }

}
