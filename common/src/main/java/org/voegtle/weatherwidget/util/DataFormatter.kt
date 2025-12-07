package org.voegtle.weatherwidget.util

import org.voegtle.weatherwidget.data.LocationDataSet
import org.voegtle.weatherwidget.data.WeatherData
import org.voegtle.weatherwidget.preferences.WidgetPreferences
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.min

private const val SEPARATOR_WIDE = " | "

class DataFormatter {
  private val numberFormat = NumberFormat.getNumberInstance(Locale.GERMANY) as DecimalFormat
  private val numberFormatShort = NumberFormat.getNumberInstance(Locale.GERMANY) as DecimalFormat
  private val integerFormat = NumberFormat.getNumberInstance(Locale.GERMANY) as DecimalFormat

  init {
    this.numberFormat.applyPattern("#,###.#")
    this.numberFormatShort.applyPattern("#,###")
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

  fun formatWidgetLine(locationDataSet: LocationDataSet): String {
    val data = locationDataSet.weatherData
    val weatherData = StringBuilder(locationDataSet.weatherLocation.shortName + " " + formatTemperature(data.temperature))
    return weatherData.toString()
  }

  fun formatWidgetLine(locationDataSet: LocationDataSet, widgetPreferences: WidgetPreferences): String {
    val data = locationDataSet.weatherData
    val weatherData = StringBuilder(locationDataSet.weatherLocation.shortName + " ")
    val weatherValues = translateToArray(widgetPreferences, data)

    if (weatherValues.isNotEmpty()) {
      weatherData.append(weatherValues[0])
      for (i in 1 until min(widgetPreferences.numberOfItems,  weatherValues.size)) {
        weatherData.append(SEPARATOR_WIDE).append(weatherValues[i])
      }
    }
    return weatherData.toString()
  }

  private fun translateToArray(
      widgetPreferences: WidgetPreferences,
      data: WeatherData
  ): ArrayList<String> {
    val weatherValues = ArrayList<String>()

    data.temperature.takeIf { widgetPreferences.showTemperature }?.let { weatherValues.add(formatTemperature(it)) }
    data.rainToday?.takeIf { widgetPreferences.showRain && it > 0.0f }?.let { weatherValues.add(formatRain(it)) }
    data.rain?.takeIf { widgetPreferences.showRainLastHour && it > 0.0f }?.let { weatherValues.add(formatRain(it)) }
    data.wind?.takeIf { widgetPreferences.showWindSpeed && it > 0.0f }?.let { weatherValues.add(formatWind(it)) }
    data.windgust?.takeIf { widgetPreferences.showWindGust && it > 0.0f }?.let { weatherValues.add(formatWind(it)) }
    data.solarradiation?.takeIf { widgetPreferences.showCurrentRadiation && it > 0.0f }?.let {weatherValues.add(formatWatt(it))}
    data.powerProduction?.takeIf { widgetPreferences.showCurrentRadiation && it > 0.0f }?.let { weatherValues.add(formatWatt(it))}
    data.humidity.takeIf { widgetPreferences.showHumidity }?.let { weatherValues.add(formatPercent(it)) }
    data.barometer.takeIf { widgetPreferences.showPressure }?.let { weatherValues.add(formatBarometer(it)) }

    return weatherValues
  }

  fun formatTemperature(temperature: Float?): String = if (temperature != null) numberFormat.format(temperature) + "°C" else ""
  fun formatHumidity(humidity: Float?): String = if (humidity != null) formatPercent(humidity) else ""

  fun formatKwh(kwh: Float?): String = if (kwh != null) numberFormat.format(kwh) + "kWh" else ""
  fun formatKwhShort(kwh: Float?): String = if (kwh != null) numberFormatShort.format(kwh) + "kWh" else ""

  fun formatRain(rain: Float?): String = if (rain != null) numberFormat.format(rain) + "l" else ""

  private fun formatPercent(value: Float?): String = if (value != null) numberFormat.format(value) + "%" else ""

  fun formatWind(value: Float?): String = if (value != null) numberFormat.format(value) + "km/h" else ""

  fun formatWatt(watt: Float?): String = if (watt != null) integerFormat.format(watt) + "W" else ""

  fun formatBarometer(barometer: Float?): String = if (barometer != null) integerFormat.format(barometer) + "hPa" else ""

  fun formatSolarradiation(solarradiation: Float?): String = if (solarradiation != null) integerFormat.format(solarradiation) + "W/m²" else ""

  fun formatInteger(value: Float?): String = if (value != null) integerFormat.format(value) else ""

  fun formatDistance(distance: Float?): String = if (distance != null) numberFormat.format(distance) + "km" else ""
}
