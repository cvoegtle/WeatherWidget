package org.voegtle.weatherwidget.util;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtil {

  public static Date getYesterday() {
    Calendar cal = Calendar.getInstance(Locale.GERMANY);
    removeTimeFraction(cal);

    cal.add(Calendar.DAY_OF_MONTH, -1);

    return cal.getTime();
  }

  public static Date getOneHoureBefore() {
    Calendar cal = Calendar.getInstance(Locale.GERMANY);
    cal.add(Calendar.HOUR, -1);
    return cal.getTime();
  }

  private static void removeTimeFraction(Calendar cal) {
    cal.set(Calendar.HOUR_OF_DAY, 0);
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.MILLISECOND, 0);
  }

  /**
   * @return age in seconds
   */
  public static int getAge(Date lastUpdate) {
    return (int) ((new Date().getTime() - lastUpdate.getTime()) / 1000);
  }

  public static Integer checkIfOutdated(Date d1, Date d2) {
    if (isOutdated(d1)) {
      return -1;
    }

    if (isOutdated(d2)) {
      return 1;
    }
    return null;
  }

  private static boolean isOutdated(Date timestamp) {
    return getAge(timestamp) > 20 * 60;
  }
}
