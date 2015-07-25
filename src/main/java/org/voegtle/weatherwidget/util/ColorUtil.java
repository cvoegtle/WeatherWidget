package org.voegtle.weatherwidget.util;

import android.graphics.Color;
import org.voegtle.weatherwidget.preferences.ColorScheme;

import java.util.Date;

public class ColorUtil {
  private static int WAITING_PERIOD = 420; // 7 Minuten
  private static int MAX_RGB_VALUE = 230;
  private static int MIN_RGB_VALUE = 20;
  private static int MIN_RGB_VALUE_DARK = 80;

  public static int byAge(ColorScheme colorScheme, Date lastUpdate) {
    return colorScheme == ColorScheme.light ? byAgeDark(lastUpdate) : byAge(lastUpdate);
  }

  public static int byAge(Date lastUpdate) {
    int age = DateUtil.getAge(lastUpdate);
    int notRed = Math.min(MAX_RGB_VALUE, Math.max(MAX_RGB_VALUE - (age - 420) / 10, MIN_RGB_VALUE));
    return Color.rgb(MAX_RGB_VALUE, notRed, notRed);
  }

  public static int byAgeDark(Date lastUpdate) {
    int age = DateUtil.getAge(lastUpdate);
    int red = Math.min(MAX_RGB_VALUE, Math.max(MIN_RGB_VALUE_DARK + (age - 420) / 10, MIN_RGB_VALUE_DARK));
    return Color.rgb(red, MIN_RGB_VALUE_DARK, MIN_RGB_VALUE_DARK);
  }

  public static int byRain(boolean isRaining, ColorScheme scheme, Date lastUpdate) {
    int age = DateUtil.getAge(lastUpdate);
    if (age < WAITING_PERIOD) {
      return isRaining ? Color.rgb(77, 140, 255) : (scheme == ColorScheme.dark ? Color.WHITE : Color.rgb(MIN_RGB_VALUE_DARK, MIN_RGB_VALUE_DARK, MIN_RGB_VALUE_DARK));
    }
    return byAge(scheme, lastUpdate);
  }


  public static int updateColor(ColorScheme scheme) {
    return scheme == ColorScheme.dark ? Color.DKGRAY : Color.GRAY;
  }

  public static int outdatedColor(ColorScheme scheme) {
    return scheme == ColorScheme.dark ? Color.GRAY : Color.DKGRAY;
  }

  public static int highlight() {
    return Color.argb(64, 128, 64, 64);
  }
}
