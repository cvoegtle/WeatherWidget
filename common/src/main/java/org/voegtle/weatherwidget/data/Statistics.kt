package org.voegtle.weatherwidget.data

import org.voegtle.weatherwidget.location.LocationIdentifier

data class Statistics(val id: LocationIdentifier, val kind: String) {

  enum class TimeRange {
    lastHour, today, yesterday, last7days, last30days;

    companion object {
      fun fromString(rangeStr: String): TimeRange? {
        return entries.firstOrNull { it.toString() == rangeStr }
      }
    }
  }

  private val statistics = HashMap<TimeRange, StatisticsSet>()

  fun add(set: StatisticsSet) {
    statistics.put(set.range, set)
  }

  operator fun get(range: TimeRange): StatisticsSet? {
    return statistics[range]
  }

  fun values(): Collection<StatisticsSet> {
    return statistics.values
  }

  companion object {
    val KIND_SOLARPOWER = "withSolarPower"
  }
}
