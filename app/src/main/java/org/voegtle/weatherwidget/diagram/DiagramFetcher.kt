package org.voegtle.weatherwidget.diagram

import android.graphics.drawable.Drawable
import android.util.Log
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class DiagramFetcher {

  private val COMMUNICATION_TIMEOUT = 60000

  fun fetchImageFromUrl(diagramId: DiagramEnum): Drawable? {
    var image: Drawable? = null
    try {
      val url = URL(diagramId.url)
      val connection = url.openConnection() as HttpURLConnection
      connection.connectTimeout = COMMUNICATION_TIMEOUT
      connection.readTimeout = COMMUNICATION_TIMEOUT
      connection.inputStream.use {
        image = createImageFromResponse(it)
      }
    } catch (e: Throwable) {
      Log.e(DiagramFetcher::class.java.toString(), "Failed to download image", e)
    }

    return image
  }

  @Throws(IOException::class)
  private fun createImageFromResponse(inputStream: InputStream): Drawable {
    val diagramImage = Drawable.createFromStream(inputStream, "Google Drive")
    return diagramImage ?: throw IOException("Image is null")
  }
}
