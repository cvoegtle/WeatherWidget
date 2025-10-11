package org.voegtle.weatherwidget.location

import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.WbCloudy
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.core.net.toUri
import org.voegtle.weatherwidget.R
import org.voegtle.weatherwidget.data.WeatherData
import org.voegtle.weatherwidget.util.DataFormatter
import org.voegtle.weatherwidget.util.DateUtil

@Composable
fun LocationComposable(locationDataSet: LocationDataSet,
    onDiagramClick: (locationIdentifier: LocationIdentifier) -> Unit = { _ -> },
    onForecastClick: (forecastUrl: Uri) -> Unit = {},
    onExpandStateChanged: (locationIdentifier: LocationIdentifier, isExpanded: Boolean) -> Unit = { _, _ -> }
) {
    Card {
        Column {
            LocationCaption(
                locationDataSet.caption,
                color = determineCaptionBackgroundColor(locationDataSet),
                locationDataSet.statistics != null,
                onDiagramClick = { onDiagramClick(locationDataSet.weatherData.location) },
                onForecastClick = { locationDataSet.weatherData.forecast?.let { onForecastClick(it.toUri()) } },
                onExpandStateChanged = { onExpandStateChanged(locationDataSet.weatherData.location, locationDataSet.statistics == null) }
            )
            LocationData(locationDataSet.weatherData, determineDataBackgroundColor(locationDataSet))
            locationDataSet.statistics?.let { StatisticsComposable(it, determineStatisticsBackgroundColor(locationDataSet)) }
        }
    }
}

@Composable
private fun determineCaptionBackgroundColor(locationDataSet: LocationDataSet): Color =
    when {
        DateUtil.isOutdated(locationDataSet.weatherData.timestamp) -> MaterialTheme.colorScheme.error
        locationDataSet.weatherLocation.preferences.favorite -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.surfaceDim
    }

@Composable
private fun determineDataBackgroundColor(locationDataSet: LocationDataSet): Color =
    when {
        locationDataSet.weatherLocation.preferences.favorite -> MaterialTheme.colorScheme.secondary
        else -> MaterialTheme.colorScheme.surfaceVariant
    }

@Composable
private fun determineStatisticsBackgroundColor(locationDataSet: LocationDataSet): Color =
    when {
        locationDataSet.weatherLocation.preferences.favorite -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.surfaceContainerHighest
    }

@Composable
fun LocationCaption(
    caption: String, color: Color = MaterialTheme.colorScheme.surfaceDim, isExpanded: Boolean,
    onDiagramClick: () -> Unit = {},
    onForecastClick: () -> Unit = {},
    onExpandStateChanged: (isExpanded: Boolean) -> Unit = {},
) {
    Surface(
        color = color,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(30.dp)
                .padding(start = 4.dp),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Text(
                text = caption,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )

            IconButton(onClick = onDiagramClick, modifier = Modifier.size(30.dp)) {
                Icon(
                    imageVector = Icons.Filled.Assessment,
                    contentDescription = stringResource(R.string.diagram_button_description)
                )
            }

            IconButton(onClick = onForecastClick, modifier = Modifier.size(30.dp)) {
                Icon(
                    imageVector = Icons.Filled.WbCloudy,
                    contentDescription = stringResource(R.string.forecast_button_description)
                )
            }

            IconButton(
                onClick = { onExpandStateChanged(!isExpanded) },
                modifier = Modifier.size(30.dp)
            ) {
                Icon(
                    imageVector = if (isExpanded) Icons.Filled.ExpandMore else Icons.Filled.ExpandLess,
                    contentDescription = stringResource(if (isExpanded) R.string.collapse_button_description else R.string.expand_button_description)
                )
            }
        }
    }
}

@Composable
fun LocationData(weatherData: WeatherData, backgroundColor: Color) {
    val formatter = DataFormatter()
    Surface(color = backgroundColor) {
        Column(modifier = Modifier.padding(all = 4.dp)) {
            DataRow(label = stringResource(R.string.temperature), value = formatter.formatTemperatureForActivity(weatherData))
            DataRow(label = stringResource(R.string.humidity), value = formatter.formatHumidityForActivity(weatherData))
            weatherData.barometer?.takeIf { it > 0.0 }?.let {
                DataRow(label = stringResource(R.string.barometer), value = formatter.formatBarometer(it))
            }
            weatherData.solarradiation?.takeIf { it > 0.0 }?.let {
                DataRow(label = stringResource(R.string.solarradiation), value = formatter.formatSolarradiation(it))
            }
            weatherData.UV?.takeIf { it > 0.0 }?.let {
                DataRow(label = stringResource(R.string.uvindex), value = formatter.formatInteger(it))
            }
            weatherData.rain?.takeIf { it > 0.0f }?.let {
                DataRow(label = stringResource(R.string.rain_last_hour), value = formatter.formatRain(it))
            }
            weatherData.rainToday?.takeIf { it > 0.0f }?.let {
                DataRow(label = stringResource(R.string.rain_today), value = formatter.formatRain(it))
            }
            weatherData.wind?.takeIf { it >= 1.0 }?.let {
                DataRow(label = stringResource(R.string.wind_speed), value = formatter.formatWind(it))
            }
            weatherData.windgust?.takeIf { it >= 10.0 }?.let {
                DataRow(label = stringResource(R.string.wind_gust), value = formatter.formatWind(it))
            }
            weatherData.watt?.takeIf { it > 0.0 }?.let {
                DataRow(label = stringResource(R.string.solar_output), value = formatter.formatWatt(it))
            }
            weatherData.powerProduction?.takeIf { it >= 1.0 }?.let {
                DataRow(label = stringResource(R.string.power_production), value = formatter.formatWatt(it))
            }
            weatherData.powerFeed?.takeIf { it >= 5.0 }?.let {
                DataRow(label = stringResource(R.string.power_feed), value = formatter.formatWatt(it))
            }
        }
    }
}

@Composable
fun DataRow(label: String, value: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, modifier = Modifier.width(calculateMaxColumnWidth()))
        Text(text = value, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun calculateMaxColumnWidth(): Dp =
    max(calculateColumnWidth(stringResource(R.string.solarradiation)),
        calculateColumnWidth(stringResource(R.string.power_production)))
