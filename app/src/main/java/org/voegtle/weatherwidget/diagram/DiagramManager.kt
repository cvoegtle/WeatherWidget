package org.voegtle.weatherwidget.diagram

import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.voegtle.weatherwidget.R
import org.voegtle.weatherwidget.util.UserFeedback

class DiagramManager(private val context: Context) {
  private val diagramCache: DiagramCache = DiagramCache(context)

  suspend fun updateDiagram(diagramId: DiagramEnum, force: Boolean = false) {
    withContext(Dispatchers.IO) {
      updateWeatherDiagram(diagramId, force)
    }
  }

  private fun updateWeatherDiagram(diagramId: DiagramEnum, force: Boolean) {
    var diagram = diagramCache.read(diagramId)
    if (diagram == null || diagram.isOld() || force) {
      try {
        val image = fetchDrawable(diagramId)
        diagram = Diagram(diagramId, image)
        diagramCache.write(diagram)
      } catch (e: Exception) {
        Log.e(DiagramManager::class.java.name, "Failed to update diagram", e)
      }
    }
  }

  private fun fetchDrawable(diagramId: DiagramEnum): Drawable {
    val image = DiagramFetcher().fetchImageFromUrl(diagramId)
    if (image == null) {
      if (context is Activity) {
        UserFeedback(context).showMessage(R.string.message_diagram_update_failed)
      }
      val message = "Fetching Image $diagramId failed"
      Log.e(DiagramManager::class.java.name, message)
      throw RuntimeException(message)
    }
    return image
  }
}
