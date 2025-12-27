package org.voegtle.weatherwidget.preferences

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import android.preference.PreferenceManager
import org.voegtle.weatherwidget.location.LocationFactory
import org.voegtle.weatherwidget.location.WeatherLocation

class WeatherPreferencesReader {

  private val context: Context

  constructor(context: Context) {
    this.context = context
  }

  fun read(): ApplicationPreferences {
      val preferences = PreferenceManager.getDefaultSharedPreferences(context)
      return ApplicationPreferences(locations = readLocations(preferences),
                                  appTheme = readAppTheme(preferences),
                                widgetPreferences = readWidgetPreferences(preferences),
                               secret = readString(preferences, "secret"))
  }

  private fun readLocations(preferences: SharedPreferences): List<WeatherLocation> {
    val locations = LocationFactory.buildWeatherLocations(context.resources)
    for (location in locations) {
      val visibleInWidgetByDefault = location.isVisibleInWidgetByDefault
      val visibleInAppByDefault = location.isVisibleInAppByDefault
      val locationPreferences = LocationPreferences(
          showInWidget = readBoolean(preferences, location.prefShowInWidget, visibleInWidgetByDefault),
          showInApp = readBoolean(preferences, location.prefShowInApp, visibleInAppByDefault),
          favorite = readBoolean(preferences, location.prefFavorite, false))
      location.preferences = locationPreferences
    }
    return locations
  }

    private fun readAppTheme(preferences: SharedPreferences): AppTheme {
        val stringValue = readString(preferences, "app_theme")
        return when (stringValue) {
            "light" -> AppTheme.LIGHT
            "dark" -> AppTheme.DARK
            else -> AppTheme.SYSTEM
        }
    }

  private fun readWidgetPreferences(preferences: SharedPreferences): WidgetPreferences {
    return WidgetPreferences(fontCorrectionFactor = readFontCorrectionFactor(preferences),
        numberOfItems = readInteger(preferences, "widget_max_values", 9),
        showTemperature = readBoolean(preferences, "widget_temperature", true),
        showRain = readBoolean(preferences, "widget_rain", true),
        showRainLastHour = readBoolean(preferences, "widget_rain_last_hour", false),
        showWindSpeed = readBoolean(preferences, "widget_wind_speed", false),
        showWindGust = readBoolean(preferences, "widget_wind_gust", false),
        showCurrentRadiation = readBoolean(preferences, "widget_current_radiation", false),
        showHumidity = readBoolean(preferences, "widget_humidity", true),
        showPressure = readBoolean(preferences, "widget_pressure", false))
  }

  private fun readFontCorrectionFactor(preferences: SharedPreferences): Int {
    val widgetFontSize = readString(preferences, "widget_font_size")
    return when (widgetFontSize) {
      "XS" -> -2
      "S" -> -1
      "M" -> 0
      "L" -> 1
      "XL" -> 3
      else -> 0
    }
  }

  private fun readString(preferences: SharedPreferences, key: String) = preferences.getString(key, "")

  private fun readInteger(preferences: SharedPreferences, key: String, defaultValue: Int): Int {
    val value = preferences.getString(key, defaultValue.toString())!!
    return Integer.valueOf(value)
  }

  private fun readBoolean(preferences: SharedPreferences, key: String, defaultValue: Boolean) = preferences.getBoolean(key, defaultValue)
}
