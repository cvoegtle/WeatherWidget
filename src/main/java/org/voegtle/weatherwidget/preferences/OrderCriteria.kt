package org.voegtle.weatherwidget.preferences

enum class OrderCriteria constructor(private val key: String) {
  location("location"), temperature("temperature"), rain("rain"), humidity("humidity");

  companion object {

    fun byKey(key: String): OrderCriteria {
      for (orderCriteria in values()) {
        if (orderCriteria.key == key) {
          return orderCriteria
        }
      }
      return location
    }

    fun byIndex(which: Int): OrderCriteria {
      if (which == 0) {
        return OrderCriteria.location
      } else if (which == 1) {
        return OrderCriteria.temperature
      } else if (which == 2) {
        return OrderCriteria.rain
      } else if (which == 3) {
        return OrderCriteria.humidity
      }
      return OrderCriteria.location
    }

    fun index(find: OrderCriteria): Int {
      return values().indexOf(find)
    }
  }

}
