package org.voegtle.weatherwidget.util;


public class StringUtil {
  public static boolean isNotEmpty(String str) {
    return str != null && !"".equals(str);
  }

  public static boolean isEmpty(String str) {
    return str == null || "".equals(str);
  }
}
