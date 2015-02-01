package org.voegtle.weatherwidget.util;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class StringUtil {
  public static boolean isNotEmpty(String str) {
    return str != null && !"".equals(str);
  }

  public static boolean isEmpty(String str) {
    return str == null || "".equals(str);
  }

  public static String urlEncode(String str) {
    String urlencoded = null;
    try {
      urlencoded = URLEncoder.encode(str, "UTF-8");
    } catch (UnsupportedEncodingException ignore) {
    }
    return urlencoded;
  }
}
