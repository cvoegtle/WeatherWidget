package org.voegtle.weatherwidget.location

import android.content.Context
import androidx.compose.foundation.layout.Column
import org.voegtle.weatherwidget.R
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.voegtle.weatherwidget.data.Statistics
import org.voegtle.weatherwidget.data.StatisticsSet
import org.voegtle.weatherwidget.util.DataFormatter

data class ColumnVisibility(
    val rain: Boolean = true,
    val minTemperature: Boolean = true,
    val maxTemperature: Boolean = true,
    val solarRadiationMax: Boolean = false,
    val kwh: Boolean = false
)

private const val WEIGHT_LABEL = 1.1f
private val MAX_LABEL = 50.dp
private const val WEIGHT_RAIN = 0.6f
private const val WEIGHT_MIN_TEMPERATURE = 0.7f
private const val WEIGHT_MAX_TEMPERATURE = 0.7f
private const val WEIGHT_MAX_SUN = 1.1f
private const val WEIGHT_KWH = 0.9f

@Composable
fun StatisticsView(statistics: Statistics) {
    val visibility = detectVisibleColumns(statistics)
    MaterialTheme {
        Column() {
            StatisticsCaptionRow(visibility, statistics.kind)
            StatisticsContentRow(statistics.get(Statistics.TimeRange.today), visibility)
            StatisticsContentRow(statistics.get(Statistics.TimeRange.yesterday), visibility)
            StatisticsContentRow(statistics.get(Statistics.TimeRange.last7days), visibility)
            StatisticsContentRow(statistics.get(Statistics.TimeRange.last30days), visibility)
        }
    }
}

@Composable
fun StatisticsCaptionRow(visibility: ColumnVisibility, kind: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "", modifier = Modifier.weight(WEIGHT_LABEL))
        if (visibility.rain) {
            TextCaption(id = R.string.rain, modifier = Modifier.weight(WEIGHT_RAIN))
        }
        if (visibility.minTemperature) {
            TextCaption(id = R.string.min_temperature, modifier = Modifier.weight(WEIGHT_MIN_TEMPERATURE))
        }
        if (visibility.maxTemperature) {
            TextCaption(id = R.string.max_temperature, modifier = Modifier.weight(WEIGHT_MAX_TEMPERATURE))
        }
        if (visibility.solarRadiationMax) {
            val captionId = if (kind == "withSolarPower") R.string.max_power_caption else R.string.solar_caption
            TextCaption(id = captionId, modifier = Modifier.weight(WEIGHT_MAX_SUN))
        }
        if (visibility.kwh) {
            val captionId = if (kind == "withSolarPower") R.string.kwh else R.string.solar_cummulated_caption
            TextCaption(id = captionId, modifier = Modifier.weight(WEIGHT_KWH))
        }
    }
}

@Composable
fun StatisticsContentRow(statisticsSet: StatisticsSet?, visibility: ColumnVisibility) {
    val context = LocalContext.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        statisticsSet?.let { statisticsSet ->
            val formatter = DataFormatter()
            TextCaption(id = timeRange2TextId(statisticsSet.range), textAlign = TextAlign.Start, modifier = Modifier.weight(WEIGHT_LABEL))
            if (visibility.rain) {
                TextContent(text = formatter.formatRain(statisticsSet.rain), modifier = Modifier.weight(WEIGHT_RAIN))
            }
            if (visibility.minTemperature) {
                TextContent(
                    text = formatter.formatTemperature(statisticsSet.minTemperature),
                    modifier = Modifier.weight(WEIGHT_MIN_TEMPERATURE)
                )
            }
            if (visibility.maxTemperature) {
                TextContent(
                    text = formatter.formatTemperature(statisticsSet.maxTemperature), modifier = Modifier.weight(
                        WEIGHT_MAX_TEMPERATURE
                    )
                )
            }
            if (visibility.solarRadiationMax) {
                TextContent(
                    text = formatter.formatSolarradiation(statisticsSet.solarRadiationMax), modifier = Modifier.weight(
                        WEIGHT_MAX_SUN
                    )
                )
            }
            if (visibility.kwh) {
                TextContent(text = formatter.formatKwhShort(statisticsSet.kwh), modifier = Modifier.weight(WEIGHT_KWH))
            }
        }
    }
}

@Composable
fun TextCaption(id: Int, textAlign: TextAlign = TextAlign.End, modifier: Modifier) {
    Text(text = stringResource(id = id), textAlign = textAlign, fontWeight = FontWeight.Bold, modifier = modifier)
}

@Composable
fun TextContent(text: String, modifier: Modifier) {
    Text(text = text, textAlign = TextAlign.End, style = MaterialTheme.typography.bodyMedium, modifier = modifier)
}


fun detectVisibleColumns(statistics: Statistics): ColumnVisibility {
    return ColumnVisibility(
        rain = statistics.values().any { it.rain != null },
        minTemperature = true,
        maxTemperature = true,
        solarRadiationMax = statistics.values().any { it.solarRadiationMax != null },
        kwh = statistics.values().any { it.kwh != null }
    )
}

fun timeRange2TextId(range: Statistics.TimeRange): Int {
    return when (range) {
        Statistics.TimeRange.lastHour -> R.string.last_hour
        Statistics.TimeRange.today -> R.string.today
        Statistics.TimeRange.yesterday -> R.string.yesterday
        Statistics.TimeRange.last7days -> R.string.week
        Statistics.TimeRange.last30days -> R.string.month
    }
}
