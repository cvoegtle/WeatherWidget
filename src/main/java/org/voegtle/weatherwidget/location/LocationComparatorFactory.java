package org.voegtle.weatherwidget.location;

import org.voegtle.weatherwidget.data.WeatherData;
import org.voegtle.weatherwidget.preferences.OrderCriteria;
import org.voegtle.weatherwidget.util.DateUtil;

import java.util.Collections;
import java.util.Comparator;

public class LocationComparatorFactory {
  public static Comparator<WeatherData> createComparator(OrderCriteria criteria) {
    Comparator<WeatherData> comparator = null;
    switch (criteria) {
      case location:
        comparator = getNaturalComparator();
        break;
      case temperature:
        comparator = Collections.reverseOrder(getDefaultComparator());
        break;
      case rain:
        comparator = Collections.reverseOrder(getRainTodayComparator());
        break;
      case humidity:
        comparator = Collections.reverseOrder(getHumidityComparator());
        break;
    }
    return comparator;
  }

  private static Comparator<WeatherData> getNaturalComparator() {
    return new Comparator<WeatherData>() {
      @Override
      public int compare(WeatherData lhs, WeatherData rhs) {
        return lhs.getLocation().compareTo(rhs.getLocation());
      }
    };
  }

  private static Comparator<WeatherData> getDefaultComparator() {
    return new Comparator<WeatherData>() {
      @Override
      public int compare(WeatherData lhs, WeatherData rhs) {
        return lhs.compareTo(rhs);
      }
    };
  }

  private static Comparator<WeatherData> getRainTodayComparator() {
    return new Comparator<WeatherData>() {
      @Override
      public int compare(WeatherData lhs, WeatherData rhs) {
        Integer outdated = DateUtil.checkIfOutdated(lhs.getTimestamp(), rhs.getTimestamp());
        if (outdated != null) {
          return outdated;
        }

        Integer nullCheckResult = checkForNullValue(lhs.getRainToday(), rhs.getRainToday());
        if (nullCheckResult != null) {
          return nullCheckResult;
        }

        return lhs.getRainToday().compareTo(rhs.getRainToday());
      }
    };
  }


  private static Comparator<WeatherData> getHumidityComparator() {
    return new Comparator<WeatherData>() {
      @Override
      public int compare(WeatherData lhs, WeatherData rhs) {
        Integer outdated = DateUtil.checkIfOutdated(lhs.getTimestamp(), rhs.getTimestamp());
        if (outdated != null) {
          return outdated;
        }

        Integer nullCheckResult = checkForNullValue(lhs.getHumidity(), rhs.getHumidity());
        if (nullCheckResult != null) {
          return nullCheckResult;
        }

        return lhs.getHumidity().compareTo(rhs.getHumidity());
      }
    };
  }

  private static Integer checkForNullValue(Float lhs, Float rhs) {
    if (lhs == null && rhs == null) {
      return 0;
    }
    if (lhs == null) {
      return -1;
    }
    if (rhs == null) {
      return 1;
    }
    return null;
  }

}
