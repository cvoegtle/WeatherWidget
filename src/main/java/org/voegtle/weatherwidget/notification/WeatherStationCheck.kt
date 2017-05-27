package org.voegtle.weatherwidget.notification

import org.voegtle.weatherwidget.data.WeatherData
import org.voegtle.weatherwidget.location.LocationIdentifier
import org.voegtle.weatherwidget.location.WeatherLocation
import org.voegtle.weatherwidget.preferences.ApplicationSettings

import java.util.ArrayList
import java.util.Date
import java.util.HashMap

class WeatherStationCheck(private val configuration: ApplicationSettings) {

  internal var alerts: MutableList<WeatherAlert> = ArrayList()

  fun checkForOverdueStations(data: HashMap<LocationIdentifier, WeatherData>): List<WeatherAlert> {
    alerts.clear()
    for (location in configuration.locations) {
      if (location.preferences.alertActive) {
        buildAlert(data[location.key])
      }
    }

    return alerts
  }

  private fun buildAlert(data: WeatherData?) {
    if (data != null) {
      val now = Date()
      if (now.time - data.timestamp!!.time > THRESHOLD) {
        alerts.add(WeatherAlert(data.location, data.timestamp!!))
      }
    }
  }

  companion object {
    private val THRESHOLD = 20 * 60 * 1000 // 20 min
  }
}
