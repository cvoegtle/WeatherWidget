package org.voegtle.weatherwidget.util

import org.voegtle.weatherwidget.data.WeatherData
import org.voegtle.weatherwidget.location.WeatherLocation

import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Locale

class DataFormatter {
  private val numberFormat: DecimalFormat

  init {
    this.numberFormat = NumberFormat.getNumberInstance(Locale.GERMANY) as DecimalFormat
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
    val formattedTemperature: String
    val temperature = data.temperature
    if (temperature != null) {
      formattedTemperature = formatTemperature(temperature)
    } else {
      formattedTemperature = "-"
    }
    return formattedTemperature
  }

  fun formatTemperature(temperature: Float?): String {
    if (temperature != null) {
      return numberFormat.format(temperature) + "°C"
    } else {
      return ""
    }
  }

  fun formatKwh(kwh: Float?): String {
    if (kwh != null) {
      return numberFormat.format(kwh) + "kWh"
    } else {
      return ""
    }
  }

  fun formatRain(rain: Float?): String {
    if (rain != null) {
      return numberFormat.format(rain) + "l"
    } else {
      return ""
    }
  }

  private fun formatPercent(`val`: Float?): String {
    if (`val` != null) {
      return numberFormat.format(`val`) + "%"
    } else {
      return ""
    }
  }

  fun formatWind(`val`: Float?): String {
    if (`val` != null) {
      return numberFormat.format(`val`) + "km/h"
    } else {
      return ""
    }

  }

  fun formatWatt(watt: Float?): String {
    if (watt != null) {
      return numberFormat.format(watt) + "W"
    } else {
      return ""
    }
  }
}
