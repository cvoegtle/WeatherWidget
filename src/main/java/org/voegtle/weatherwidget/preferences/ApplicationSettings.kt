package org.voegtle.weatherwidget.preferences

import org.voegtle.weatherwidget.location.LocationIdentifier
import org.voegtle.weatherwidget.location.WeatherLocation

data class ApplicationSettings(var locations: List<WeatherLocation> = ArrayList<WeatherLocation>(),
  var secret: String? = null,
  var updateIntervall: Int? = null,
  var widgetTextSize: Int? = null,
  var isShowInfoNotification: Boolean = false,
  var colorScheme: ColorScheme = ColorScheme.dark) {

  val appTextSize: Int?
    get() = widgetTextSize!! + widgetTextSize!! / 4

  fun setTextSize(textSize: Int?) {
    this.widgetTextSize = textSize
  }

  fun findLocation(identifier: LocationIdentifier): WeatherLocation? {
    return locations.firstOrNull { it.key == identifier }
  }
}
