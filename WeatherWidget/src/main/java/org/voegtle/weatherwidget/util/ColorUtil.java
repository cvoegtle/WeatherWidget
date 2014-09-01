package org.voegtle.weatherwidget.util;

import android.graphics.Color;

import java.util.Date;

public class ColorUtil {
  public static int byAge(Date lastUpdate) {
    int age = (int) ((new Date().getTime() - lastUpdate.getTime()) / 1000);
    int notRed = Math.min(255, Math.max(255 - (age - 180) / 10, 0));
    return Color.rgb(255, notRed, notRed);
  }

  public static int byRain(boolean isRaining) {
    return isRaining ? Color.rgb(77, 140, 255) : Color.WHITE;
  }
}
