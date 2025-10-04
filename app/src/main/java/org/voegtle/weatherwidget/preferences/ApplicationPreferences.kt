package org.voegtle.weatherwidget.preferences

import org.voegtle.weatherwidget.location.LocationIdentifier
import org.voegtle.weatherwidget.location.WeatherLocation

data class ApplicationPreferences(var locations: List<WeatherLocation> = ArrayList(),
                                  val widgetPreferences: WidgetPreferences,
                                  val secret: String? = null) {

  fun findLocation(identifier: LocationIdentifier): WeatherLocation? {
    return locations.firstOrNull { it.key == identifier }
  }
}
