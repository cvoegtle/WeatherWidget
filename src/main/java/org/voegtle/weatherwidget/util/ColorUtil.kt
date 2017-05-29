package org.voegtle.weatherwidget.util

import android.graphics.Color
import org.voegtle.weatherwidget.preferences.ColorScheme

import java.util.Date

object ColorUtil {
  private val WAITING_PERIOD = 420 // 7 Minuten
  private val MAX_RGB_VALUE = 230
  private val MIN_RGB_VALUE = 20
  private val MIN_RGB_VALUE_DARK = 80

  fun byAge(colorScheme: ColorScheme, lastUpdate: Date): Int {
    return if (colorScheme === ColorScheme.light) byAgeDark(lastUpdate) else byAge(lastUpdate)
  }

  fun byAge(lastUpdate: Date): Int {
    val age = DateUtil.getAge(lastUpdate)
    val notRed = Math.min(MAX_RGB_VALUE, Math.max(MAX_RGB_VALUE - (age - 420) / 10, MIN_RGB_VALUE))
    return Color.rgb(MAX_RGB_VALUE, notRed, notRed)
  }

  private fun byAgeDark(lastUpdate: Date): Int {
    val age = DateUtil.getAge(lastUpdate)
    val red = Math.min(MAX_RGB_VALUE, Math.max(MIN_RGB_VALUE_DARK + (age - 420) / 10, MIN_RGB_VALUE_DARK))
    return Color.rgb(red, MIN_RGB_VALUE_DARK, MIN_RGB_VALUE_DARK)
  }

  fun byRain(isRaining: Boolean, scheme: ColorScheme, lastUpdate: Date): Int {
    val age = DateUtil.getAge(lastUpdate)
    if (age < WAITING_PERIOD) {
      return if (isRaining) Color.rgb(77, 140, 255) else if (scheme === ColorScheme.dark) Color.WHITE else Color.rgb(MIN_RGB_VALUE_DARK, MIN_RGB_VALUE_DARK, MIN_RGB_VALUE_DARK)
    }
    return byAge(scheme, lastUpdate)
  }


  fun updateColor(scheme: ColorScheme): Int {
    return if (scheme === ColorScheme.dark) Color.DKGRAY else Color.GRAY
  }

  fun outdatedColor(scheme: ColorScheme): Int {
    return if (scheme === ColorScheme.dark) Color.GRAY else Color.DKGRAY
  }

  fun highlight(): Int {
    return Color.argb(64, 128, 64, 64)
  }
}