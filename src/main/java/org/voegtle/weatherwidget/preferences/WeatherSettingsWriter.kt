package org.voegtle.weatherwidget.preferences

import android.content.SharedPreferences

class WeatherSettingsWriter {
  fun enableNotification(preferences: SharedPreferences) {
    val editor = preferences.edit()
    editor.putBoolean("info_notification", true)
    editor.apply()
  }
}
