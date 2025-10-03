package org.voegtle.weatherwidget.widget

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.text.FontWeight
import org.voegtle.weatherwidget.location.LocationDataSet
import org.voegtle.weatherwidget.util.DataFormatter

class TemperatureWidget : BaseWeatherWidget() {

    override fun assembleWeatherText(
        locationDataSet: LocationDataSet,
        formatter: DataFormatter
    ): String = formatter.formatWidgetLine(locationDataSet, false)

    override fun determineFontSize(locationDataSets: List<LocationDataSet>): TextUnit = 17.sp
    override fun determineFontWeight(): FontWeight = FontWeight.Bold
    override fun determineGap(): Dp = 1.dp
}
