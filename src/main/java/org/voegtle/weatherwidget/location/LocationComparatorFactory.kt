package org.voegtle.weatherwidget.location

import org.voegtle.weatherwidget.data.WeatherData
import org.voegtle.weatherwidget.preferences.OrderCriteria
import org.voegtle.weatherwidget.util.DateUtil

import java.util.Collections
import java.util.Comparator

object LocationComparatorFactory {
  fun createComparator(criteria: OrderCriteria): Comparator<WeatherData> =
    when (criteria) {
      OrderCriteria.location -> naturalComparator
      OrderCriteria.temperature -> Collections.reverseOrder(defaultComparator)
      OrderCriteria.rain -> Collections.reverseOrder(rainTodayComparator)
      OrderCriteria.humidity -> Collections.reverseOrder(humidityComparator)
      else -> naturalComparator
    }

  val naturalComparator: Comparator<WeatherData> = Comparator { lhs, rhs -> lhs.location.compareTo(rhs.location) }

  val defaultComparator: Comparator<WeatherData> = Comparator { lhs, rhs -> lhs.compareTo(rhs) }

  val rainTodayComparator: Comparator<WeatherData>
    get() = Comparator { lhs, rhs ->
      val outdated = DateUtil.checkIfOutdated(lhs.timestamp, rhs.timestamp)
      if (outdated != null) {
        return@Comparator outdated
      }

      val nullCheckResult = checkForNullValue(lhs.rainToday, rhs.rainToday)
      if (nullCheckResult != null) {
        return@Comparator nullCheckResult
      }

      lhs.rainToday!!.compareTo(rhs.rainToday!!)
    }


  val humidityComparator: Comparator<WeatherData>
    = Comparator { lhs, rhs ->
      val outdated = DateUtil.checkIfOutdated(lhs.timestamp, rhs.timestamp)
      if (outdated != null) {
        return@Comparator outdated
      }

      val nullCheckResult = checkForNullValue(lhs.humidity, rhs.humidity)
      if (nullCheckResult != null) {
        return@Comparator nullCheckResult
      }

      lhs.humidity.compareTo(rhs.humidity)
    }

  private fun checkForNullValue(lhs: Float?, rhs: Float?): Int? {
    if (lhs == null && rhs == null) {
      return 0
    }
    if (lhs == null) {
      return -1
    }
    if (rhs == null) {
      return 1
    }
    return null
  }

}
