package org.voegtle.weatherwidget.preferences

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import org.voegtle.weatherwidget.location.LocationFactory
import org.voegtle.weatherwidget.location.WeatherLocation

class WeatherSettingsReader {

  private var resources: Resources? = null

  constructor(context: Context) {
    this.resources = context.resources
  }

  constructor(resources: Resources) {
    this.resources = resources
  }

  fun read(preferences: SharedPreferences): ApplicationSettings {
    val configuration = ApplicationSettings()
    val locations = readLocations(preferences)
    configuration.locations = locations
    configuration.secret = getString(preferences, "secret")
    configuration.isShowInfoNotification = getBoolean(preferences, "info_notification", false)
    configuration.updateIntervall = getInteger(preferences, "update_interval", 30)
    configuration.setTextSize(getInteger(preferences, "text_size", 11))
    configuration.colorScheme = getColorScheme(preferences, "color_scheme", ColorScheme.dark)

    return configuration
  }

  private fun getColorScheme(preferences: SharedPreferences, key: String, defaultScheme: ColorScheme): ColorScheme {
    val value = getString(preferences, key)
    var scheme = ColorScheme.byKey(value)
    if (scheme == null) {
      scheme = defaultScheme
    }
    return scheme
  }

  private fun readLocations(preferences: SharedPreferences): List<WeatherLocation> {
    val locations = LocationFactory.buildWeatherLocations(resources!!)
    for (location in locations) {
      val visibleInWidgetByDefault = location.isVisibleInWidgetByDefault
      val visibleInAppByDefault = location.isVisibleInAppByDefault
      val locationPreferences = LocationPreferences(
          getBoolean(preferences, location.prefShowInWidget, visibleInWidgetByDefault),
          getBoolean(preferences, location.prefShowInApp, visibleInAppByDefault),
          getBoolean(preferences, location.prefAlert, false))
      location.preferences = locationPreferences
    }
    return locations
  }


  private fun getInteger(preferences: SharedPreferences, key: String, defaultValue: Int): Int? {
    val value = preferences.getString(key, Integer.toString(defaultValue))
    return Integer.valueOf(value)
  }

  private fun getBoolean(preferences: SharedPreferences, key: String, defaultValue: Boolean): Boolean {
    return preferences.getBoolean(key, defaultValue)
  }

  private fun getString(preferences: SharedPreferences, key: String): String {
    return preferences.getString(key, "")
  }

}
