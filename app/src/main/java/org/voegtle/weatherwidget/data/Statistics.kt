package org.voegtle.weatherwidget.data

data class Statistics(val id: String, val kind: String) {

  enum class TimeRange {
    lastHour, today, yesterday, last7days, last30days;

    companion object {
      fun fromString(rangeStr: String): TimeRange? {
        return entries.firstOrNull { it.toString() == rangeStr }
      }
    }
  }

  private val statistics = HashMap<Statistics.TimeRange, StatisticsSet>()

  fun add(set: StatisticsSet) {
    statistics.put(set.range, set)
  }

  operator fun get(range: TimeRange): StatisticsSet? {
    return statistics[range]
  }

  fun values(): Collection<StatisticsSet> {
    return statistics.values
  }
}
