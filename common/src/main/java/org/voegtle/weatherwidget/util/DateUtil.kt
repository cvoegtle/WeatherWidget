package org.voegtle.weatherwidget.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateUtil {

  val yesterday: Date
    get() {
      val cal = Calendar.getInstance(Locale.GERMANY)
      removeTimeFraction(cal)

      cal.add(Calendar.DAY_OF_MONTH, -1)

      return cal.time
    }

  val oneHouerBefore: Date
    get() {
      val cal = Calendar.getInstance(Locale.GERMANY)
      cal.add(Calendar.HOUR, -1)
      return cal.time
    }

  val currentTime: String
    get(){
      val df = SimpleDateFormat("HH:mm", Locale.GERMANY)
      return df.format(Date())
    }

  private fun removeTimeFraction(cal: Calendar) {
    cal.set(Calendar.HOUR_OF_DAY, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MILLISECOND, 0)
  }

  /**
   * @return age in seconds
   */
  fun getAge(lastUpdate: Date): Int {
    return ((Date().time - lastUpdate.time) / 1000).toInt()
  }

  fun checkIfOutdated(d1: Date, d2: Date): Int? =
      when {
        isOutdated(d1) -> -1
        isOutdated(d2) -> 1
        else -> null
      }

  fun isOutdated(timestamp: Date): Boolean = getAge(timestamp) > 20 * 60
  fun isMinimumTimeSinceLastUpdate(timeOfLastUpdate: Date): Boolean {
    return getAge(timeOfLastUpdate)  > 3
  }
}
