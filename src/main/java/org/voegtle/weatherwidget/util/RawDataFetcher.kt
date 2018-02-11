package org.voegtle.weatherwidget.util

import android.util.Log
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

data class RawData(val valid: Boolean, val data: String = "")

object RawDataFetcher {
  private val COMMUNICATION_TIMEOUT = 60000

  fun getStringFromUrl(uri: String): RawData {
    try {
      val url = URL(uri)
      val connection = url.openConnection() as HttpURLConnection
      try {
        connection.connectTimeout = COMMUNICATION_TIMEOUT
        connection.readTimeout = COMMUNICATION_TIMEOUT
        val response = readStream(connection.inputStream)
        return RawData(true, response)
      } finally {
        connection.disconnect()
      }
    } catch (e: Throwable) {
      Log.d(WeatherDataFetcher::class.java.toString(), "Failed to download weather data", e)
      return RawData(false)
    }
  }

  @Throws(IOException::class)
  private fun readStream(content: InputStream): String {
    val builder = StringBuilder()
    val reader = BufferedReader(InputStreamReader(content, "UTF-8"))
    var line: String? = reader.readLine()
    while (line != null) {
      builder.append(line)
      line = reader.readLine()
    }
    reader.close()
    return builder.toString()
  }

}