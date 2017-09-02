package org.voegtle.weatherwidget.preferences

import org.voegtle.weatherwidget.location.LocationIdentifier
import org.voegtle.weatherwidget.location.WeatherLocation

data class ApplicationSettings(var locations: List<WeatherLocation> = ArrayList<WeatherLocation>(),
                               val secret: String? = null,
                               val widgetTextSize: Int = 11,
                               val colorScheme: ColorScheme = ColorScheme.dark) {

  val appTextSize: Int
    get() = widgetTextSize + widgetTextSize / 4

  fun findLocation(identifier: LocationIdentifier): WeatherLocation? {
    return locations.firstOrNull { it.key == identifier }
  }
}
