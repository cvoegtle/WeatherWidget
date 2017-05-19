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

    override fun compareTo(another: WeatherData): Int {
        val outdated = DateUtil.checkIfOutdated(timestamp, another.timestamp)
        if (outdated != null) {
            return outdated
        }

        return Objects.compare(temperature, another.temperature)
    }
}
