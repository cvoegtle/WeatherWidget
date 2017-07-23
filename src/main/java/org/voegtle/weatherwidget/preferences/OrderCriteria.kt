package org.voegtle.weatherwidget.preferences

enum class OrderCriteria constructor(private val key: String) {
  location("location"), temperature("temperature"), rain("rain"), humidity("humidity"), default("default");

  companion object {

    fun byKey(key: String): OrderCriteria {
      return values().firstOrNull { it.key == key } ?: location
    }

    fun byIndex(which: Int): OrderCriteria =
        when (which) {
          0 -> OrderCriteria.location
          1 -> OrderCriteria.temperature
          2 -> OrderCriteria.rain
          3 -> OrderCriteria.humidity
          else -> OrderCriteria.default
        }

    fun index(find: OrderCriteria): Int {
      return values().indexOf(find)
    }
  }

}
