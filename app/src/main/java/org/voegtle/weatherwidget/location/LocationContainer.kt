package org.voegtle.weatherwidget.location

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.input.nestedscroll.NestedScrollSource.Companion.UserInput
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import org.voegtle.weatherwidget.data.Statistics
import org.voegtle.weatherwidget.data.WeatherData
import org.voegtle.weatherwidget.ui.theme.WeatherWidgetTheme

data class LocationDataSet(val weatherLocation: WeatherLocation, var caption: String, val weatherData: WeatherData, var statistics: Statistics?)

class LocationContainer(val context: Context, private val container: ComposeView) {

    fun showWeatherData(
        locationDataSets: List<LocationDataSet>,
        onDiagramClick: (locationIdentifier: LocationIdentifier) -> Unit = {},
        onForecastClick: (forecastUrl: Uri) -> Unit = {},
        onExpandStateChanged: (locationIdentifier: LocationIdentifier, isExpanded: Boolean) -> Unit = { _, _ -> },
        onPullToRefresh: (overscrollAmount: Float) -> Unit = {}
    ) {
        container.setContent {
            WeatherWidgetTheme {
                val lazyListState = rememberLazyListState()
                val nestedScrollConnection = remember {
                    object : NestedScrollConnection {
                        override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset {
                            if (source == NestedScrollSource.UserInput) {
                                if (available.y > 0) {
                                    onPullToRefresh(available.y)
                                }
                            }
                            return Offset.Zero
                        }
                    }
                }

                LazyColumn(
                    state = lazyListState,
                    modifier = Modifier.nestedScroll(nestedScrollConnection)
                ) {
                    items(items = locationDataSets) { dataSet ->
                        LocationComposable(
                            dataSet,
                            onDiagramClick = onDiagramClick, onForecastClick = onForecastClick, onExpandStateChanged = onExpandStateChanged
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
            }
        }
    }
}
