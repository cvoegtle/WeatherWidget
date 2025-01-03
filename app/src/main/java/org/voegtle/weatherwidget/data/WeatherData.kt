package org.voegtle.weatherwidget.data

import android.net.Uri
import org.voegtle.weatherwidget.location.LocationIdentifier
import org.voegtle.weatherwidget.location.Position
import org.voegtle.weatherwidget.util.DateUtil
import java.util.*

data class WeatherData(
    val location: LocationIdentifier,
    val timestamp: Date,
    val temperature: Float,
    val humidity: Float,
    val localtime: String,
    val position: Position,
    val insideTemperature: Float?,
    val insideHumidity: Float?,
    val barometer: Float?,
    val solarradiation: Float?,
    val UV: Float?,
    val rain: Float?,
    val rainToday: Float?,
    val isRaining: Boolean,
    val watt: Float?,
    val powerProduction: Float?,
    val powerFeed: Float?,
    val wind: Float?,
    val windgust: Float?,
    val location_name: String,
    val location_short: String,
    val forecast: String?
) : Comparable<WeatherData> {


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
