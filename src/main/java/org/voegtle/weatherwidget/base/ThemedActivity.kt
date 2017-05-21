package org.voegtle.weatherwidget.base

import android.app.Activity
import android.os.Bundle
import android.preference.PreferenceManager
import org.voegtle.weatherwidget.preferences.ColorScheme
import org.voegtle.weatherwidget.preferences.WeatherSettingsReader

abstract class ThemedActivity : Activity() {
  var colorScheme = ColorScheme.dark

  override fun onCreate(savedInstanceState: Bundle?) {
    configureTheme()
    super.onCreate(savedInstanceState)
  }

  private fun configureTheme() {
    val preferences = PreferenceManager.getDefaultSharedPreferences(this)
    val weatherSettingsReader = WeatherSettingsReader(this.applicationContext)
    val configuration = weatherSettingsReader.read(preferences)
    colorScheme = configuration.colorScheme
    setTheme(colorScheme.theme)
  }
}
