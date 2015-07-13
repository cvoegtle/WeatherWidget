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
}
