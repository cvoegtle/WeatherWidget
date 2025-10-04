package org.voegtle.weatherwidget.widget

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.text.FontWeight
import org.voegtle.weatherwidget.location.LocationDataSet
import org.voegtle.weatherwidget.util.DataFormatter

class WeatherDetailsWidget : BaseWeatherWidget() {

    override fun assembleWeatherText(
        locationDataSet: LocationDataSet,
        formatter: DataFormatter
    ): String = formatter.formatWidgetLine(locationDataSet, true)

    override fun determineFontSize(locationDataSets: List<LocationDataSet>) =
        if (locationDataSets.any { it.weatherData.rainToday != null })  12.sp else 13.sp
    override fun determineFontWeight(): FontWeight = FontWeight.Normal
    override fun determineGap(): Dp = 3.dp

}
