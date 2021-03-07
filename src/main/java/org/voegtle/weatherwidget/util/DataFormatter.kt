package org.voegtle.weatherwidget.util

import org.voegtle.weatherwidget.data.WeatherData
import org.voegtle.weatherwidget.location.WeatherLocation
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

class DataFormatter {
  private val numberFormat = NumberFormat.getNumberInstance(Locale.GERMANY) as DecimalFormat
  private val integerFormat = NumberFormat.getNumberInstance(Locale.GERMANY) as DecimalFormat

  init {
    this.numberFormat.applyPattern("#,###.#")
    this.integerFormat.applyPattern("#,###")
  }

  fun formatTemperatureForActivity(data: WeatherData): String {
    val builder = StringBuilder()
    builder.append(formatTemperature(data.temperature))
    if (data.insideTemperature != null) {
      builder.append(" / ").append(formatTemperature(data.insideTemperature))
    }
    return builder.toString()
  }

  fun formatHumidityForActivity(data: WeatherData): String {
    val builder = StringBuilder()
    builder.append(formatPercent(data.humidity))
    if (data.insideHumidity != null) {
      builder.append(" / ").append(formatPercent(data.insideHumidity))
    }
    return builder.toString()
  }

  fun formatWidgetLine(location: WeatherLocation, data: WeatherData, detailed: Boolean): String {
    val weatherData = StringBuilder(location.shortName + " " + formatTemperature(data))
    if (detailed) {
      weatherData.append(" | ")
      weatherData.append(formatPercent(data.humidity))
      data.rainToday?.let {
        weatherData.append(" | ").append(formatRain(it))
      }
    }

    return weatherData.toString()
  }

  fun formatTemperature(data: WeatherData): String {
    return formatTemperature(data.temperature)
  }

  fun formatTemperature(temperature: Float?): String = if (temperature != null) numberFormat.format(temperature) + "°C" else ""

  fun formatKwh(kwh: Float?): String = if (kwh != null) numberFormat.format(kwh) + "kWh" else ""

  fun formatRain(rain: Float?): String = if (rain != null) numberFormat.format(rain) + "l" else ""

  private fun formatPercent(value: Float?): String = if (value != null) numberFormat.format(value) + "%" else ""

  fun formatWind(value: Float?): String = if (value != null) numberFormat.format(value) + "km/h" else ""

  fun formatWatt(watt: Float?): String = if (watt != null) integerFormat.format(watt) + "W" else ""

  fun formatBarometer(barometer: Float?): String = if (barometer != null) integerFormat.format(barometer) + "hPa" else ""

  fun formatSolarradiation(solarradiation: Float?): String = if (solarradiation != null) integerFormat.format(solarradiation) + "W/m²" else ""

  fun formatInteger(value: Float?): String = if (value != null) integerFormat.format(value) else ""

  fun formatDistance(distance: Float?): String = if (distance != null) numberFormat.format(distance) + "km" else ""
}
