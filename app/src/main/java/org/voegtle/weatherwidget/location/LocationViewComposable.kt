package org.voegtle.weatherwidget.location

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme // Material 2
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import org.voegtle.weatherwidget.R
import org.voegtle.weatherwidget.data.Statistics
import org.voegtle.weatherwidget.data.StatisticsSet
import org.voegtle.weatherwidget.data.WeatherData
import org.voegtle.weatherwidget.util.DataFormatter
import java.util.Date

@Composable
fun LocationViewComposable(
    caption: String,
    captionColor: Color = MaterialTheme.colors.onSurface,
    weatherData: WeatherData?,
    statistics: Statistics?,
    formatter: DataFormatter,
    showDiagramButton: Boolean = true,
    showForecastButton: Boolean = true,
    highlightTrigger: Boolean = false, // NEUER Parameter
    onHighlightFinished: () -> Unit = {}, // NEUER Parameter
    onDiagramClick: () -> Unit = {},
    onForecastClick: () -> Unit = {},
    onExpandStateChanged: (isExpanded: Boolean) -> Unit = {},
    modifier: Modifier = Modifier 
) {
    var isExpanded by remember { mutableStateOf(false) }
    var isHighlighted by remember { mutableStateOf(false) }

    val animatedHighlightColor by animateColorAsState(
        targetValue = if (isHighlighted) MaterialTheme.colors.secondary.copy(alpha = 0.3f) else Color.Transparent,
        animationSpec = tween(durationMillis = 300)
    )

    LaunchedEffect(highlightTrigger) {
        if (highlightTrigger) {
            isHighlighted = true
            delay(1000L) 
            isHighlighted = false
            onHighlightFinished() 
        }
    }

    Column(
        modifier = modifier 
            .background(animatedHighlightColor) 
            .fillMaxWidth()
            .padding(vertical = 4.dp) 
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp), 
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = caption,
                style = MaterialTheme.typography.h2,
                color = captionColor,
                modifier = Modifier.weight(1f)
            )

            if (showDiagramButton) {
                IconButton(onClick = onDiagramClick) {
                    Icon(
                        imageVector = Icons.Filled.Assessment,
                        contentDescription = stringResource(R.string.diagram_button_description)
                    )
                }
            }

            if (showForecastButton) {
                IconButton(onClick = onForecastClick) {
                    Icon(
                        imageVector = Icons.Filled.Visibility,
                        contentDescription = stringResource(R.string.forecast_button_description)
                    )
                }
            }

            IconButton(onClick = {
                isExpanded = !isExpanded
                onExpandStateChanged(isExpanded)
            }) {
                Icon(
                    imageVector = if (isExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = stringResource(if (isExpanded) R.string.collapse_button_description else R.string.expand_button_description)
                )
            }
        }

        if (isExpanded) {
            CurrentWeatherDetails(
                weatherData = weatherData,
                formatter = formatter,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            StatisticsDetails(
                statistics = statistics,
                formatter = formatter,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
    }
}

@Composable
fun CurrentWeatherDetails(
    weatherData: WeatherData?,
    formatter: DataFormatter,
    modifier: Modifier = Modifier
) {
    weatherData ?: return

    Column(modifier = modifier) {
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

@Composable
fun StatisticsDetails(
    statistics: Statistics?,
    formatter: DataFormatter,
    modifier: Modifier = Modifier
) {
    statistics ?: return

    val showRainCaption = remember(statistics) { statistics.values().any { it.rain != null } }
    val showKwhCaption = remember(statistics) { statistics.values().any { it.kwh != null } }
    val showSolarCaption = remember(statistics) { statistics.values().any { it.solarRadiationMax != null } }

    val kwhCaptionText = if (statistics.kind == "withSolarPower") stringResource(R.string.kwh)
    else stringResource(R.string.solar_cummulated_caption)
    val solarCaptionText = if (statistics.kind == "withSolarPower") stringResource(R.string.max_power_caption)
    else stringResource(R.string.solar_caption)


    Column(modifier = modifier) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("", modifier = Modifier.weight(1f)) 
            if (showRainCaption) {
                Text(
                    stringResource(R.string.rain),
                    style = MaterialTheme.typography.subtitle1,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
            }
            Text(
                stringResource(R.string.min_temperature),
                style = MaterialTheme.typography.subtitle1,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            Text(
                stringResource(R.string.max_temperature),
                style = MaterialTheme.typography.subtitle1,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            if (showKwhCaption) {
                Text(kwhCaptionText, style = MaterialTheme.typography.subtitle1, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            }
            if (showSolarCaption) {
                Text(solarCaptionText, style = MaterialTheme.typography.subtitle1, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            }
        }
        Spacer(modifier = Modifier.height(4.dp))

        StatisticsDataRow(
            periodLabel = stringResource(R.string.today),
            statsSet = statistics[Statistics.TimeRange.today],
            kind = statistics.kind,
            formatter = formatter,
            showRain = showRainCaption,
            showKwh = showKwhCaption,
            showSolar = showSolarCaption
        )
        StatisticsDataRow(
            periodLabel = stringResource(R.string.yesterday),
            statsSet = statistics[Statistics.TimeRange.yesterday],
            kind = statistics.kind,
            formatter = formatter,
            showRain = showRainCaption,
            showKwh = showKwhCaption,
            showSolar = showSolarCaption
        )
        StatisticsDataRow(
            periodLabel = stringResource(R.string.week),
            statsSet = statistics[Statistics.TimeRange.last7days],
            kind = statistics.kind,
            formatter = formatter,
            showRain = showRainCaption,
            showKwh = showKwhCaption,
            showSolar = showSolarCaption
        )
        StatisticsDataRow(
            periodLabel = stringResource(R.string.month),
            statsSet = statistics[Statistics.TimeRange.last30days],
            kind = statistics.kind,
            formatter = formatter,
            showRain = showRainCaption,
            showKwh = showKwhCaption,
            showSolar = showSolarCaption
        )
    }
}

@Composable
fun DataRow(label: String, value: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.body2, fontWeight = FontWeight.Bold)
        Text(text = value, style = MaterialTheme.typography.body2)
    }
}

@Composable
fun StatisticsDataRow(
    periodLabel: String,
    statsSet: StatisticsSet?,
    kind: String,
    formatter: DataFormatter,
    showRain: Boolean,
    showKwh: Boolean,
    showSolar: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(periodLabel, style = MaterialTheme.typography.body2, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))

        if (showRain) {
            Text(statsSet?.rain?.let { formatter.formatRain(it) } ?: "", style = MaterialTheme.typography.body2, modifier = Modifier.weight(1f))
        }
        Text(formatter.formatTemperature(statsSet?.minTemperature), style = MaterialTheme.typography.body2, modifier = Modifier.weight(1f))
        Text(formatter.formatTemperature(statsSet?.maxTemperature), style = MaterialTheme.typography.body2, modifier = Modifier.weight(1f))

        if (showKwh) {
            Text(statsSet?.kwh?.let { if (it < 1000.0f) formatter.formatKwh(it) else formatter.formatKwhShort(it) } ?: "",
                style = MaterialTheme.typography.body2,
                modifier = Modifier.weight(1f))
        }
        if (showSolar) {
            val solarValue = statsSet?.solarRadiationMax?.let {
                if (kind == "withSolarPower") formatter.formatWatt(it) else formatter.formatSolarradiation(it)
            } ?: ""
            Text(solarValue, style = MaterialTheme.typography.body2, modifier = Modifier.weight(1f))
        }
    }
}

@Preview(showBackground = true, name = "LocationView Expanded Preview")
@Composable
fun LocationViewPreviewExpanded() {
    val dummyWeatherData = WeatherData(
        location = LocationIdentifier.Paderborn,
        location_name = "Paderborn",
        location_short = "PB",
        timestamp = Date(),
        localtime = "12:10",
        position = Position(51.723779f, 8.758523f),
        temperature = 25.5f,
        humidity = 60f,
        barometer = 1012.5f,
        solarradiation = 800f,
        UV = 7f,
        rain = 0.5f,
        rainToday = 2.0f,
        wind = 15f,
        windgust = 30f,
        powerProduction = 1500f,
        insideHumidity = null,
        insideTemperature = null,
        watt = null,
        powerFeed = null,
        isRaining = true,
        forecast = "https://www.voegtle.org/",
    )
    val dummyStatistics = Statistics("Freiburg", "withSolarPower").apply {
      add(
        StatisticsSet(
            Statistics.TimeRange.today,
            rain = 1.0f,
            minTemperature = 10.0f,
            maxTemperature = 20.0f,
            kwh = 5.0f,
            solarRadiationMax = 600f,
        )
    )
    add(
        StatisticsSet(
            Statistics.TimeRange.yesterday,
            rain = 1.0f,
            minTemperature = 10.0f,
            maxTemperature = 20.0f,
            kwh = 5.0f,
            solarRadiationMax = 600f,
        )
    )
    add(
        StatisticsSet(
            Statistics.TimeRange.last7days,
            rain = 1.0f,
            minTemperature = 10.0f,
            maxTemperature = 20.0f,
            kwh = 5.0f,
            solarRadiationMax = 600f,
        )
    )
    add(
        StatisticsSet(
            Statistics.TimeRange.last30days,
            rain = 1.0f,
            minTemperature = 10.0f,
            maxTemperature = 20.0f,
            kwh = 5.0f,
            solarRadiationMax = 600f,
        )
    )}

    val dummyFormatter = DataFormatter()

    MaterialTheme {
        LocationViewComposable(
            caption = "Freiburg (Highlighted Test)",
            weatherData = dummyWeatherData,
            statistics = dummyStatistics,
            formatter = dummyFormatter,
            highlightTrigger = true, 
            onHighlightFinished = {}
        )
    }
}


@Preview(showBackground = true, name = "LocationView Collapsed Preview")
@Composable
fun LocationViewPreviewCollapsed() {
    MaterialTheme {
        LocationViewComposable(
            caption = "Bonn",
            weatherData = null, 
            statistics = null,  
            formatter = DataFormatter(),
            showDiagramButton = false,
            showForecastButton = true
        )
    }
}
