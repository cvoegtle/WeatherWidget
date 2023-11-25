package org.voegtle.weatherwidget.preferences

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import org.voegtle.weatherwidget.location.LocationFactory
import org.voegtle.weatherwidget.location.WeatherLocation

class WeatherSettingsReader {

  private val resources: Resources

  constructor(context: Context) {
    this.resources = context.resources
  }

  constructor(resources: Resources) {
    this.resources = resources
  }

  fun read(preferences: SharedPreferences): ApplicationSettings {
    return ApplicationSettings(locations = readLocations(preferences),
                               secret = readString(preferences, "secret"),
                               widgetTextSize = readInteger(preferences, "text_size", 11),
                               colorScheme = readColorScheme(preferences, "color_scheme", ColorScheme.dark))
  }

  private fun readColorScheme(preferences: SharedPreferences, key: String, defaultScheme: ColorScheme): ColorScheme {
    val schemeKey = readString(preferences, key)!!
    return ColorScheme.byKey(schemeKey) ?: defaultScheme
  }

  private fun readLocations(preferences: SharedPreferences): List<WeatherLocation> {
    val locations = LocationFactory.buildWeatherLocations(resources)
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

  private fun readString(preferences: SharedPreferences, key: String) = preferences.getString(key, "")

  private fun readInteger(preferences: SharedPreferences, key: String, defaultValue: Int): Int {
    val value = preferences.getString(key, defaultValue.toString())!!
    return Integer.valueOf(value)
  }

  private fun readBoolean(preferences: SharedPreferences, key: String, defaultValue: Boolean) = preferences.getBoolean(key, defaultValue)
}
