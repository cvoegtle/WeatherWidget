package org.voegtle.weatherwidget.location

import org.voegtle.weatherwidget.data.WeatherData
import org.voegtle.weatherwidget.preferences.OrderCriteria
import org.voegtle.weatherwidget.util.DateUtil
import java.util.Collections

object LocationComparatorFactory {
  fun createComparator(criteria: OrderCriteria, userPosition: Position): Comparator<LocationDataSet> =
      when (criteria) {
        OrderCriteria.location -> locationComparator(userPosition)
        OrderCriteria.temperature -> Collections.reverseOrder(temperatureComparator)
        OrderCriteria.rain -> Collections.reverseOrder(rainTodayComparator)
        OrderCriteria.humidity -> Collections.reverseOrder(humidityComparator)
        OrderCriteria.default -> defaultComparator
      }

  val defaultComparator: Comparator<LocationDataSet> = Comparator { lhs, rhs -> lhs.weatherData.location.compareTo(rhs.weatherData.location) }

  val temperatureComparator: Comparator<LocationDataSet> = Comparator { lhs, rhs -> lhs.weatherData.compareTo(rhs.weatherData) }

  fun locationComparator(userPosition: Position): Comparator<LocationDataSet> = Comparator<LocationDataSet> { lhs, rhs ->
    val lhsDistance = userPosition.distanceTo(lhs.weatherData.position)
    val rhsDistance = userPosition.distanceTo(rhs.weatherData.position)
    lhsDistance.compareTo(rhsDistance)
  }

  val rainTodayComparator: Comparator<LocationDataSet>
    get() = Comparator { lhs, rhs ->
      DateUtil.checkIfOutdated(lhs.weatherData.timestamp, rhs.weatherData.timestamp)?.let {
        return@Comparator it
      }

      checkForNullValue(lhs.weatherData.rainToday, rhs.weatherData.rainToday)?.let {
        return@Comparator it
      }

      lhs.weatherData.rainToday!!.compareTo(rhs.weatherData.rainToday!!)
    }


  val humidityComparator: Comparator<LocationDataSet> = Comparator { lhs, rhs ->
    DateUtil.checkIfOutdated(lhs.weatherData.timestamp, rhs.weatherData.timestamp)?.let {
      return@Comparator it
    }

    lhs.weatherData.humidity.compareTo(rhs.weatherData.humidity)
  }

  private fun checkForNullValue(lhs: Float?, rhs: Float?): Int? =
      when {
        lhs == null && rhs == null -> 0
        lhs == null -> -1
        rhs == null -> 1
        else -> null
      }
}
