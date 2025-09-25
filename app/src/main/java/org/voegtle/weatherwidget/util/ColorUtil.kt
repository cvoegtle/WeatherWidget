package org.voegtle.weatherwidget.util

import android.graphics.Color
import java.util.Date

object ColorUtil {
  private val WAITING_PERIOD = 420 // 7 Minuten
  private val MAX_RGB_VALUE = 230
  private val MIN_RGB_VALUE = 20
  private val MIN_RGB_VALUE_DARK = 80

  fun byAgeDark(lastUpdate: Date): Int {
    val age = DateUtil.getAge(lastUpdate)
    val red = Math.min(MAX_RGB_VALUE, Math.max(MIN_RGB_VALUE_DARK + (age - 420) / 10, MIN_RGB_VALUE_DARK))
    return Color.rgb(red, MIN_RGB_VALUE_DARK, MIN_RGB_VALUE_DARK)
  }

  fun highlightText(): Int = Color.rgb(235, 130, 45)

  fun byRain(isRaining: Boolean, lastUpdate: Date): Int {
    val age = DateUtil.getAge(lastUpdate)
    when {
      age > WAITING_PERIOD -> return byAgeDark(lastUpdate)
      isRaining -> return Color.rgb(77, 140, 255)
      else -> return Color.rgb(MIN_RGB_VALUE_DARK, MIN_RGB_VALUE_DARK, MIN_RGB_VALUE_DARK)
    }
  }

  fun updateColor(): Int = Color.GRAY

  fun highlight(): Int = Color.argb(64, 128, 64, 64)
}
