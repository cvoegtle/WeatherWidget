package org.voegtle.weatherwidget.notification

import org.voegtle.weatherwidget.data.WeatherData
import org.voegtle.weatherwidget.location.LocationIdentifier
import org.voegtle.weatherwidget.preferences.ApplicationSettings
import java.util.Date

class WeatherStationCheck(private val configuration: ApplicationSettings) {
  private val THRESHOLD = 20 * 60 * 1000 // 20 min

  private val alerts: MutableList<WeatherAlert> = ArrayList()

  fun checkForOverdueStations(data: HashMap<LocationIdentifier, WeatherData>): List<WeatherAlert> {
    alerts.clear()
    configuration.locations
        .filter { it.preferences.alertActive }
        .forEach { buildAlert(data[it.key]) }

    return alerts
  }

  private fun buildAlert(data: WeatherData?) {
    data?.let {
      val now = Date()
      if (now.time - it.timestamp.time > THRESHOLD) {
        alerts.add(WeatherAlert(it.location, it.timestamp))
      }
    }
  }

}
