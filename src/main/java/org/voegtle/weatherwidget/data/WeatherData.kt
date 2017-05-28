package org.voegtle.weatherwidget.data

import org.voegtle.weatherwidget.location.LocationIdentifier
import org.voegtle.weatherwidget.util.DateUtil
import java.util.*

class WeatherData(val location: LocationIdentifier,
                  val timestamp: Date,
                  val temperature: Float,
                  var humidity: Float) : Comparable<WeatherData> {

  var localtime: String? = null
  var insideTemperature: Float? = null
  var insideHumidity: Float? = null
  var rain: Float? = null
  var rainToday: Float? = null
  var isRaining: Boolean = false
  var watt: Float? = null
  var wind: Float? = null

  override fun compareTo(other: WeatherData): Int {
    val outdated = DateUtil.checkIfOutdated(timestamp, other.timestamp)
    if (outdated != null) {
      return outdated
    }
    val anotherTemperature = other.temperature
    val temp = temperature
    return temp.compareTo(anotherTemperature)
  }
}
