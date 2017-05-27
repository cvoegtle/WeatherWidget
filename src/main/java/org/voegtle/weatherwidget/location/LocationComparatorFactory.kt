package org.voegtle.weatherwidget.location

import org.voegtle.weatherwidget.data.WeatherData
import org.voegtle.weatherwidget.preferences.OrderCriteria
import org.voegtle.weatherwidget.util.DateUtil

import java.util.Collections
import java.util.Comparator

object LocationComparatorFactory {
  fun createComparator(criteria: OrderCriteria): Comparator<WeatherData> {
    var comparator: Comparator<WeatherData>
    when (criteria) {
      OrderCriteria.location -> comparator = naturalComparator
      OrderCriteria.temperature -> comparator = Collections.reverseOrder(defaultComparator)
      OrderCriteria.rain -> comparator = Collections.reverseOrder(rainTodayComparator)
      OrderCriteria.humidity -> comparator = Collections.reverseOrder(humidityComparator)
      else -> comparator = naturalComparator
    }
    return comparator
  }

  private val naturalComparator: Comparator<WeatherData>
    get() = Comparator { lhs, rhs -> lhs.location.compareTo(rhs.location) }

  private val defaultComparator: Comparator<WeatherData>
    get() = Comparator { lhs, rhs -> lhs.compareTo(rhs) }

  private val rainTodayComparator: Comparator<WeatherData>
    get() = Comparator { lhs, rhs ->
      val outdated = DateUtil.checkIfOutdated(lhs.timestamp!!, rhs.timestamp!!)
      if (outdated != null) {
        return@Comparator outdated
      }

      val nullCheckResult = checkForNullValue(lhs.rainToday, rhs.rainToday)
      if (nullCheckResult != null) {
        return@Comparator nullCheckResult
      }

      lhs.rainToday!!.compareTo(rhs.rainToday!!)
    }


  private val humidityComparator: Comparator<WeatherData>
    get() = Comparator { lhs, rhs ->
      val outdated = DateUtil.checkIfOutdated(lhs.timestamp!!, rhs.timestamp!!)
      if (outdated != null) {
        return@Comparator outdated
      }

      val nullCheckResult = checkForNullValue(lhs.humidity, rhs.humidity)
      if (nullCheckResult != null) {
        return@Comparator nullCheckResult
      }

      lhs.humidity!!.compareTo(rhs.humidity!!)
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
