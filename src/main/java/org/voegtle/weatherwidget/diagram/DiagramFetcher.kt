package org.voegtle.weatherwidget.diagram

import android.graphics.drawable.Drawable
import android.util.Log

import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLConnection

class DiagramFetcher {

  private val COMMUNICATION_TIMEOUT = 60000

  fun fetchImageFromUrl(diagramId: DiagramEnum): Drawable? {
    var image: Drawable? = null
    try {
      val url = URL(diagramId.url)
      val connection = url.openConnection() as HttpURLConnection
      connection.connectTimeout = COMMUNICATION_TIMEOUT
      connection.readTimeout = COMMUNICATION_TIMEOUT
      val input = connection.inputStream
      try {
        image = createImageFromResponse(input)
      } finally {
        input.close()
        connection.disconnect()
      }
    } catch (e: Throwable) {
      Log.e(DiagramFetcher::class.java.toString(), "Failed to download image", e)
    }

    return image
  }

  @Throws(IOException::class)
  private fun createImageFromResponse(inputStream: InputStream): Drawable {
    return Drawable.createFromStream(inputStream, "Google Drive")
  }
}