package org.voegtle.weatherwidget.preferences

enum class OrderCriteria(private val key: String) {
  default("default"), location("location"), temperature("temperature"), rain("rain"), humidity("humidity");

  companion object {

    fun byKey(key: String): OrderCriteria {
      return entries.firstOrNull { it.key == key } ?: default
    }

    fun byIndex(which: Int): OrderCriteria =
        when (which) {
          0-> default
          1 -> location
          2 -> temperature
          3 -> rain
          4 -> humidity
          else -> default
        }

    fun index(find: OrderCriteria): Int {
      return entries.indexOf(find)
    }
  }

}
