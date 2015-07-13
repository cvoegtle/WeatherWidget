package org.voegtle.weatherwidget.preferences;

public enum OrderCriteria {
  location("location"), temperature("temperature"), rain("rain"), humidity("humidity");

  private String key;

  OrderCriteria(String key) {
    this.key = key;
  }

  public static OrderCriteria byKey(String key) {
    for (OrderCriteria criteria : values()) {
      if (criteria.key.equals(key)) {
        return criteria;
      }
    }
    return null;
  }

  public static OrderCriteria byIndex(int which) {
    if (which == 0) {
      return OrderCriteria.location;
    } else if (which == 1) {
      return OrderCriteria.temperature;
    } else if (which == 2) {
      return OrderCriteria.rain;
    } else if (which == 3) {
      return OrderCriteria.humidity;
    }
    return OrderCriteria.location;
  }

  public static int index(OrderCriteria find) {
    int i = 0;
    for (OrderCriteria orderCriteria : values()) {
      if (orderCriteria.equals(find)) {
        return i;
      }
      i++;
    }
    return -1;
  }

}
