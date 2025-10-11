package org.voegtle.weatherwidget.location

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.MutableStateFlow
import org.voegtle.weatherwidget.R
import org.voegtle.weatherwidget.data.Statistics
import org.voegtle.weatherwidget.data.WeatherData

data class LocationDataSet(val weatherLocation: WeatherLocation, var caption: String, val weatherData: WeatherData, var statistics: Statistics?)

@Composable
fun LocationContainer(
    locationDataSets: MutableStateFlow<List<LocationDataSet>>,
    onDiagramClick: (LocationIdentifier) -> Unit = {},
    onForecastClick: (Uri) -> Unit = {},
    onExpandStateChanged: (LocationIdentifier, Boolean) -> Unit = { _, _ -> },
    onPullToRefresh: (Float) -> Unit = {},
    onDataMiningButtonClick: () -> Unit = {}
) {
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
        items(items = locationDataSets.value) { dataSet ->
            LocationComposable(
                dataSet,
                onDiagramClick = onDiagramClick, onForecastClick = onForecastClick, onExpandStateChanged = onExpandStateChanged
            )
            Spacer(modifier = Modifier.height(4.dp))
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(onClick = {
                    onDataMiningButtonClick()
                }) {
                    Text(text = stringResource(R.string.data_mining))
                }
            }
        }
    }
}
