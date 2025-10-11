package org.voegtle.weatherwidget.location

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import org.voegtle.weatherwidget.R
import org.voegtle.weatherwidget.data.Statistics
import org.voegtle.weatherwidget.data.StatisticsSet
import org.voegtle.weatherwidget.util.DataFormatter


data class ColumnVisibility(
    val rain: Boolean = true,
    val minTemperature: Boolean = true,
    val maxTemperature: Boolean = true,
    val solarRadiationMax: Boolean = false,
    val kwh: Boolean = false
) {
    fun onlyBasicColumns() = !(solarRadiationMax || kwh)
}

private const val WEIGHT_MAX_SUN = 1.1f
private const val WEIGHT_KWH = 1.0f

@Composable
fun StatisticsComposable(statistics: Statistics, color: Color) {
    val visibility = detectVisibleColumns(statistics)
    Surface(color = color) {
        Column(modifier = Modifier.fillMaxWidth().padding(4.dp)) {
            StatisticsCaptionRow(statistics.kind, visibility)
            StatisticsContentRow(statistics[Statistics.TimeRange.today], statistics.kind, visibility)
            StatisticsContentRow(statistics[Statistics.TimeRange.yesterday], statistics.kind, visibility)
            StatisticsContentRow(statistics[Statistics.TimeRange.last7days], statistics.kind, visibility)
            StatisticsContentRow(statistics[Statistics.TimeRange.last30days], statistics.kind, visibility)
        }
    }
}

@Composable
fun StatisticsCaptionRow(kind: String, visibility: ColumnVisibility) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        val labelColumnWidth = calculateLabelColumnWidth(visibility)
        Text(text = "", modifier = Modifier.width(labelColumnWidth))

        val fixedColumnWidth = calculateDefaultColumnWidth(visibility)
        if (visibility.rain) {
            TextCaption(id = R.string.rain, modifier = Modifier.width(fixedColumnWidth))
        }
        if (visibility.minTemperature) {
            TextCaption(id = R.string.min_temperature, modifier = Modifier.width(fixedColumnWidth))
        }
        if (visibility.maxTemperature) {
            TextCaption(id = R.string.max_temperature, modifier = Modifier.width(fixedColumnWidth))
        }
        if (visibility.solarRadiationMax) {
            val captionId = if (kind == Statistics.KIND_SOLARPOWER) R.string.max_power_caption else R.string.solar_caption
            TextCaption(id = captionId, modifier = Modifier.weight(WEIGHT_MAX_SUN))
        }
        if (visibility.kwh) {
            val captionId = if (kind == Statistics.KIND_SOLARPOWER) R.string.kwh else R.string.solar_cummulated_caption
            TextCaption(id = captionId, modifier = Modifier.weight(WEIGHT_KWH))
        }
    }
}

@Composable
fun StatisticsContentRow(statisticsSet: StatisticsSet?, kind: String, visibility: ColumnVisibility) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        statisticsSet?.let { statisticsSet ->
            val formatter = DataFormatter()
            val labelColumnWidth = calculateLabelColumnWidth(visibility)
            TextCaption(
                id = timeRange2TextId(statisticsSet.range), textAlign = TextAlign.Start,
                modifier = Modifier.width(labelColumnWidth)
            )
            val fixedColumnWidth = calculateDefaultColumnWidth(visibility)
            if (visibility.rain) {
                TextContent(text = formatter.formatRain(statisticsSet.rain), modifier = Modifier.width(fixedColumnWidth))
            }
            if (visibility.minTemperature) {
                TextContent(
                    text = formatter.formatTemperature(statisticsSet.minTemperature),
                    modifier = Modifier.width(fixedColumnWidth)
                )
            }
            if (visibility.maxTemperature) {
                TextContent(text = formatter.formatTemperature(statisticsSet.maxTemperature), modifier = Modifier.width(fixedColumnWidth)
                )
            }
            if (visibility.solarRadiationMax) {
                val maxPower = getMaxPowerText(statisticsSet)
                TextContent(text = maxPower, modifier = Modifier.weight(WEIGHT_MAX_SUN))
            }
            if (visibility.kwh) {
                val totalPower = getTotalPowerText(kind, statisticsSet)
                TextContent(text = totalPower, modifier = Modifier.weight(WEIGHT_KWH))
            }
        }
    }
}

@Composable
fun TextCaption(id: Int, textAlign: TextAlign = TextAlign.End, modifier: Modifier) {
    Text(text = stringResource(id = id), textAlign = textAlign, fontWeight = FontWeight.Bold, fontSize = MaterialTheme.typography.bodyLarge.fontSize, modifier = modifier)
}

@Composable
fun TextContent(text: String, modifier: Modifier) {
    Text(text = text, textAlign = TextAlign.End, style = MaterialTheme.typography.bodyMedium, modifier = modifier)
}

@Composable
private fun calculateDefaultColumnWidth(visibility: ColumnVisibility): Dp {
    return  calculateColumnWidth("Regen", visibility)
}

@Composable
private fun calculateLabelColumnWidth(visibility: ColumnVisibility): Dp {
    return calculateColumnWidth(stringResource(R.string.yesterday), visibility)
}

@Composable
fun calculateColumnWidth(text: String, visibility: ColumnVisibility): Dp {
    val textMeasurer = rememberTextMeasurer()
    val textLayoutResult = textMeasurer.measure(text = text,
        style = TextStyle(fontSize = MaterialTheme.typography.bodyLarge.fontSize, fontWeight = FontWeight.Bold))
    return with( LocalDensity.current) { textLayoutResult.size.width.toDp() + 5.dp + if (visibility.onlyBasicColumns()) 5.dp else 0.dp }
}
@Composable
fun calculateColumnWidth(text: String): Dp {
    val textMeasurer = rememberTextMeasurer()
    val textLayoutResult = textMeasurer.measure(text = text,
        style = TextStyle(fontSize = MaterialTheme.typography.bodyLarge.fontSize, fontWeight = FontWeight.Bold))
    return with( LocalDensity.current) { textLayoutResult.size.width.toDp() + 5.dp }
}

private fun getTotalPowerText(kind: String, statisticsSet: StatisticsSet): String {
    val formatter = DataFormatter()
    val totalPower = if (kind == Statistics.KIND_SOLARPOWER)
        formatter.formatKwhShort(statisticsSet.kwh)
    else
        formatter.formatKwh(statisticsSet.kwh)
    return totalPower
}

private fun getMaxPowerText(statisticsSet: StatisticsSet): String {
    val formatter = DataFormatter()
    return formatter.formatWatt(statisticsSet.solarRadiationMax)
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
