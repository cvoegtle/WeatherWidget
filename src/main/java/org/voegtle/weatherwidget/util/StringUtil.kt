package org.voegtle.weatherwidget.util


import java.io.UnsupportedEncodingException
import java.net.URLEncoder

object StringUtil {
  fun isNotEmpty(str: String?): Boolean {
    return str != null && "" != str
  }

  fun isEmpty(str: String?): Boolean {
    return str == null || "" == str
  }

  fun urlEncode(str: String): String {
    var urlencoded: String = ""
    try {
      urlencoded = URLEncoder.encode(str, "UTF-8")
    } catch (ignore: UnsupportedEncodingException) {
    }

    return urlencoded
  }
}
