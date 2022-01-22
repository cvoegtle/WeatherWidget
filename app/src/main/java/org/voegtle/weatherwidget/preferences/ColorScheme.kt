package org.voegtle.weatherwidget.preferences

import org.voegtle.weatherwidget.R

enum class ColorScheme constructor(private val key: String, val theme: Int) {
  dark("dark", R.style.AppTheme),
  light("light", R.style.AppThemeLight);


  companion object {
    fun byKey(key: String) = values().firstOrNull { it.key == key }
  }

}
