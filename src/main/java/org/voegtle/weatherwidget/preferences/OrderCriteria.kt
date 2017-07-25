package org.voegtle.weatherwidget.preferences

enum class OrderCriteria constructor(private val key: String) {
  default("default"), location("location"), temperature("temperature"), rain("rain"), humidity("humidity");

  companion object {

    fun byKey(key: String): OrderCriteria {
      return values().firstOrNull { it.key == key } ?: default
    }

    fun byIndex(which: Int): OrderCriteria =
        when (which) {
          0-> OrderCriteria.default
          1 -> OrderCriteria.location
          2 -> OrderCriteria.temperature
          3 -> OrderCriteria.rain
          4 -> OrderCriteria.humidity
          else -> OrderCriteria.default
        }

    fun index(find: OrderCriteria): Int {
      return values().indexOf(find)
    }
  }

}
