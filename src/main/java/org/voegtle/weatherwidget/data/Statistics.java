package org.voegtle.weatherwidget.data;

import java.util.Collection;
import java.util.HashMap;

public class Statistics {

  public enum TimeRange {
    lastHour, today, yesterday, last7days, last30days;

    public static TimeRange fromString(String rangeStr) {
      for (TimeRange range : values()) {
        if (range.toString().equals(rangeStr)) {
          return range;
        }
      }
      return null;
    }
  }

  private String id;

  private HashMap<TimeRange, StatisticsSet> statistics = new HashMap<>();

  public void add(StatisticsSet set) {
    statistics.put(set.getRange(), set);
  }

  public StatisticsSet get(TimeRange range) {
    return statistics.get(range);
  }

  public Collection<StatisticsSet> values() {
    return statistics.values();
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }
}
