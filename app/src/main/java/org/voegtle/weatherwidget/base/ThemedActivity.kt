package org.voegtle.weatherwidget.base

import android.os.Bundle
import android.preference.PreferenceManager
import androidx.fragment.app.FragmentActivity
import org.voegtle.weatherwidget.preferences.ColorScheme
import org.voegtle.weatherwidget.preferences.WeatherSettingsReader

abstract class ThemedActivity : FragmentActivity() {
  var colorScheme = ColorScheme.dark

  override fun onCreate(savedInstanceState: Bundle?) {
    configureTheme()
    super.onCreate(savedInstanceState)
  }

  private fun configureTheme() {
    val preferences = PreferenceManager.getDefaultSharedPreferences(this)
    val weatherSettingsReader = WeatherSettingsReader(this.applicationContext)
    val configuration = weatherSettingsReader.read(preferences)
    this.colorScheme = configuration.colorScheme
    setTheme(colorScheme.theme)
  }
}
