package org.voegtle.weatherwidget.widget

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.text.FontWeight
import org.voegtle.weatherwidget.data.LocationDataSet
import org.voegtle.weatherwidget.preferences.WidgetPreferences
import org.voegtle.weatherwidget.util.DataFormatter

class TemperatureWidget : BaseWeatherWidget() {

    @Composable
    override fun assembleWeatherText(
        locationDataSet: LocationDataSet,
        widgetPreferences: WidgetPreferences
    ): String = DataFormatter().formatWidgetLine(locationDataSet)

    @Composable
    override fun determineFontSize(locationDataSets: List<LocationDataSet>, widgetPreferences: WidgetPreferences): TextUnit = 17.sp
    override fun determineFontWeight(): FontWeight = FontWeight.Bold
    override fun determineGap(): Dp = 2.dp
    override fun determinePadding(): Dp = 0.dp
    override fun isSettingsButtonVisible(): Boolean = false
}
