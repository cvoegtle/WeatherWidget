package org.voegtle.weatherwidget.widget

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.text.FontWeight
import org.voegtle.weatherwidget.location.LocationDataSet
import org.voegtle.weatherwidget.preferences.WidgetPreferences
import org.voegtle.weatherwidget.util.DataFormatter

private const val DEFAULT_FONTSIZE = 13

class WeatherDetailsWidget : BaseWeatherWidget() {

    @Composable
    override fun assembleWeatherText(
        locationDataSet: LocationDataSet,
        widgetPreferences: WidgetPreferences
    ): String {
        val formatter = DataFormatter()
        return formatter.formatWidgetLine(locationDataSet, widgetPreferences)
    }

    @Composable
    override fun determineFontSize(locationDataSets: List<LocationDataSet>, widgetPreferences: WidgetPreferences): TextUnit {
        return (DEFAULT_FONTSIZE + widgetPreferences.fontCorrectionFactor).sp
    }
    override fun determineFontWeight(): FontWeight = FontWeight.Normal
    override fun determineGap(): Dp = 2.dp
    override fun determinePadding(): Dp = 1.dp
}
