package org.voegtle.weatherwidget.location

import org.voegtle.weatherwidget.data.WeatherData
import org.voegtle.weatherwidget.preferences.OrderCriteria
import org.voegtle.weatherwidget.util.DateUtil
import java.util.Collections

object LocationComparatorFactory {
  fun createComparator(criteria: OrderCriteria, userPosition: Position): Comparator<WeatherData> =
      when (criteria) {
        OrderCriteria.location -> locationComparator(userPosition)
        OrderCriteria.temperature -> Collections.reverseOrder(temperatureComparator)
        OrderCriteria.rain -> Collections.reverseOrder(rainTodayComparator)
        OrderCriteria.humidity -> Collections.reverseOrder(humidityComparator)
        OrderCriteria.default -> defaultComparator
      }

  val defaultComparator: Comparator<WeatherData> = Comparator { (location), rhs -> location.compareTo(rhs.location) }

  val temperatureComparator: Comparator<WeatherData> = Comparator { lhs, rhs -> lhs.compareTo(rhs) }

  fun locationComparator(userPosition: Position): Comparator<WeatherData> = Comparator<WeatherData> { lhs, rhs ->
    val lhsDistance = userPosition.distanceTo(lhs.position)
    val rhsDistance = userPosition.distanceTo(rhs.position)
    lhsDistance.compareTo(rhsDistance)
  }

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
