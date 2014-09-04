package org.voegtle.weatherwidget.util;

import android.graphics.Color;

import java.util.Date;

public class ColorUtil {
  private static int WAITING_PERIOD = 420; // 7 Minuten

  public static int byAge(Date lastUpdate) {
    int age = getAge(lastUpdate);
    int notRed = Math.min(255, Math.max(255 - (age - 420) / 10, 0));
    return Color.rgb(255, notRed, notRed);
  }

  public static int byRain(boolean isRaining, Date lastUpdate) {
    int age = getAge(lastUpdate);
    if (age < WAITING_PERIOD) {
      return isRaining ? Color.rgb(77, 140, 255) : Color.WHITE;
    }
    return byAge(lastUpdate);
  }

  private static int getAge(Date lastUpdate) {
    return (int) ((new Date().getTime() - lastUpdate.getTime()) / 1000);
  }


}
