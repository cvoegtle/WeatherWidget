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

  public static Date getToday() {
    Calendar cal = Calendar.getInstance(Locale.GERMANY);
    removeTimeFraction(cal);

    return cal.getTime();
  }

  private static void removeTimeFraction(Calendar cal) {
    cal.set(Calendar.HOUR_OF_DAY, 0);
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.MILLISECOND, 0);
  }




}
