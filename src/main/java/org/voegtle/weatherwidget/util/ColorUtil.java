package org.voegtle.weatherwidget.util;

import android.graphics.Color;

import java.util.Date;

public class ColorUtil {
  private static int WAITING_PERIOD = 420; // 7 Minuten
  private static int MAX_RGB_VALUE = 230;
  private static int MIN_RGB_VALUE = 20;

  public static int byAge(Date lastUpdate) {
    int age = getAge(lastUpdate);
    int notRed = Math.min(MAX_RGB_VALUE, Math.max(MAX_RGB_VALUE - (age - 420) / 10, MIN_RGB_VALUE));
    return Color.rgb(MAX_RGB_VALUE, notRed, notRed);
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


  public static int updateColor() {
    return Color.DKGRAY;
  }

  public static int outdatedColor() {
    return Color.GRAY;
  }
}
