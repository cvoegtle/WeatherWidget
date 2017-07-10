package org.voegtle.weatherwidget.location

import org.voegtle.weatherwidget.data.WeatherData
import org.voegtle.weatherwidget.preferences.OrderCriteria
import org.voegtle.weatherwidget.util.DateUtil
import java.util.*

object LocationComparatorFactory {
  fun createComparator(criteria: OrderCriteria): Comparator<WeatherData> =
      when (criteria) {
        OrderCriteria.location -> naturalComparator
        OrderCriteria.temperature -> Collections.reverseOrder(defaultComparator)
        OrderCriteria.rain -> Collections.reverseOrder(rainTodayComparator)
        OrderCriteria.humidity -> Collections.reverseOrder(humidityComparator)
      }

  val naturalComparator: Comparator<WeatherData> = Comparator { (location), rhs -> location.compareTo(rhs.location) }

  val defaultComparator: Comparator<WeatherData> = Comparator { lhs, rhs -> lhs.compareTo(rhs) }

  val rainTodayComparator: Comparator<WeatherData>
    get() = Comparator { lhs, rhs ->
      DateUtil.checkIfOutdated(lhs.timestamp, rhs.timestamp)?.let {
        return@Comparator it
      }

      checkForNullValue(lhs.rainToday, rhs.rainToday)?.let {
        return@Comparator it
      }

      lhs.rainToday!!.compareTo(rhs.rainToday!!)
    }


  val humidityComparator: Comparator<WeatherData> = Comparator { lhs, rhs ->
    DateUtil.checkIfOutdated(lhs.timestamp, rhs.timestamp)?.let {
      return@Comparator it
    }

    lhs.humidity.compareTo(rhs.humidity)
  }

  private fun checkForNullValue(lhs: Float?, rhs: Float?): Int? =
      when {
        lhs == null && rhs == null -> 0
        lhs == null -> -1
        rhs == null -> 1
        else -> null
      }
}
