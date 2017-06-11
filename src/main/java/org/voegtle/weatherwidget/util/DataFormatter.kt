package org.voegtle.weatherwidget.util

import org.voegtle.weatherwidget.data.WeatherData
import org.voegtle.weatherwidget.location.WeatherLocation
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

class DataFormatter {
  private val numberFormat = NumberFormat.getNumberInstance(Locale.GERMANY) as DecimalFormat

  init {
    this.numberFormat.applyPattern("###.#")
  }


  fun formatTemperatureForActivity(data: WeatherData): String {
    val builder = StringBuilder()
    builder.append(formatTemperature(data.temperature))
    if (data.insideTemperature != null) {
      builder.append(" / ")
      builder.append(formatTemperature(data.insideTemperature))
    }
    return builder.toString()
  }

  fun formatHumidityForActivity(data: WeatherData): String {
    val builder = StringBuilder()
    builder.append(formatPercent(data.humidity))
    if (data.insideHumidity != null) {
      builder.append(" / ")
      builder.append(formatPercent(data.insideHumidity))
    }
    return builder.toString()
  }

  fun formatWidgetLine(location: WeatherLocation, data: WeatherData, detailed: Boolean): String {
    val weatherData = StringBuilder(location.shortName + " "
        + formatTemperature(data))
    if (detailed) {
      weatherData.append(" | ")
      weatherData.append(formatPercent(data.humidity))
      if (data.rainToday != null) {
        weatherData.append(" | ")
        weatherData.append(formatRain(data.rainToday))
      }
    }

    return weatherData.toString()
  }

  fun formatTemperature(data: WeatherData): String {
    return formatTemperature(data.temperature)
  }

  fun formatTemperature(temperature: Float?): String {
    return if (temperature != null) numberFormat.format(temperature) + "°C" else ""
  }

  fun formatKwh(kwh: Float?): String {
    return if (kwh != null) numberFormat.format(kwh) + "kWh" else ""
  }

  fun formatRain(rain: Float?): String {
    return if (rain != null) numberFormat.format(rain) + "l" else ""
  }

  private fun formatPercent(value: Float?): String {
    return if (value != null) numberFormat.format(value) + "%" else ""
  }

  fun formatWind(value: Float?): String {
    return if (value != null) numberFormat.format(value) + "km/h" else ""
  }

  fun formatWatt(watt: Float?): String {
    return if (watt != null) numberFormat.format(watt) + "W" else ""
  }
}
