package org.voegtle.weatherwidget.data

import org.voegtle.weatherwidget.location.LocationIdentifier
import org.voegtle.weatherwidget.util.DateUtil
import java.util.*

class WeatherData(val location: LocationIdentifier) : Comparable<WeatherData> {
    var timestamp: Date? = null
    var localtime: String? = null
    var temperature: Float? = null
    var insideTemperature: Float? = null
    var humidity: Float? = null
    var insideHumidity: Float? = null
    var rain: Float? = null
    var rainToday: Float? = null
    var isRaining: Boolean = false
    var watt: Float? = null
    var wind: Float? = null

    override fun compareTo(other: WeatherData): Int {
        val outdated = DateUtil.checkIfOutdated(timestamp!!, other.timestamp!!)
        if (outdated != null) {
            return outdated
        }
        val anotherTemperature = other.temperature
        val temp = temperature
        if (temp == null) {
            return -1
        } else if (anotherTemperature == null) {
            return 1
        } else {
            return temp.compareTo(anotherTemperature)
        }
    }
}
