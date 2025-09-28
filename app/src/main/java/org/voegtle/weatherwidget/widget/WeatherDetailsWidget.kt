package org.voegtle.weatherwidget.widget

import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import androidx.glance.text.FontWeight
import org.voegtle.weatherwidget.location.LocationDataSet
import org.voegtle.weatherwidget.util.DataFormatter

class WeatherDetailsWidget : BaseWeatherWidget() {

    override fun assembleWeatherText(
        locationDataSet: LocationDataSet,
        formatter: DataFormatter
    ): String = formatter.formatWidgetLine(locationDataSet, true)

    override fun determineFontSize(): TextUnit = 13.sp
    override fun determineFontWeight(): FontWeight = FontWeight.Normal

}
