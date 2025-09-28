package org.voegtle.weatherwidget.widget

import org.voegtle.weatherwidget.location.LocationDataSet
import org.voegtle.weatherwidget.util.DataFormatter

class LargeGlanceWidget : BaseWeatherGlanceWidget() {

    override fun assembleWeatherText(
        locationDataSet: LocationDataSet,
        formatter: DataFormatter
    ): String = formatter.formatWidgetLine(locationDataSet, true)

}
