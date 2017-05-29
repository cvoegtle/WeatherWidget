package org.voegtle.weatherwidget.diagram

import android.graphics.drawable.Drawable
import android.preference.PreferenceManager
import android.support.v4.content.ContextCompat
import android.util.Log
import android.widget.ImageView
import org.voegtle.weatherwidget.R
import org.voegtle.weatherwidget.preferences.ColorScheme
import org.voegtle.weatherwidget.preferences.WeatherSettingsReader
import org.voegtle.weatherwidget.util.UserFeedback
import uk.co.senab.photoview.PhotoViewAttacher
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class DiagramManager(private val fragment: DiagramFragment) {
  private val diagramCache: DiagramCache
  private var placeholderImage: Drawable? = null
  private var active = false

  init {
    this.diagramCache = DiagramCache(fragment.activity)
  }

  fun onResume() {
    configureTheme()

    active = true
  }

  private fun configureTheme() {
    val context = fragment.activity.applicationContext
    val preferences = PreferenceManager.getDefaultSharedPreferences(context)
    val weatherSettingsReader = WeatherSettingsReader(context)
    val configuration = weatherSettingsReader.read(preferences)
    if (configuration.colorScheme == ColorScheme.dark) {
      placeholderImage = ContextCompat.getDrawable(context, R.drawable.ic_action_picture)
    } else {
      placeholderImage = ContextCompat.getDrawable(context, R.drawable.ic_action_picture_dark)
    }
  }

  fun onPause() {
    active = false
  }

  @JvmOverloads fun updateDiagram(diagramId: DiagramEnum, force: Boolean = false) {
    val updater = Runnable { updateWeatherDiagram(diagramId, force) }
    val scheduler = Executors.newScheduledThreadPool(1)
    scheduler.schedule(updater, 0, TimeUnit.SECONDS)
  }

  private var inProgress: Boolean = false

  private fun updateWeatherDiagram(diagramId: DiagramEnum, force: Boolean) {
    if (inProgress) {
      return
    }
    try {
      inProgress = true

      var diagram = diagramCache.read(diagramId)
      if (diagram == null || diagram.isOld() || force) {
        showDrawable(placeholderImage!!)
        val image = fetchDrawable(diagramId)
        diagram = Diagram(diagramId, image)

        diagramCache.write(diagram)
      }

      showDiagram(diagram)
    } finally {
      inProgress = false
    }
  }

  private fun fetchDrawable(diagramId: DiagramEnum): Drawable {
    val image = DiagramFetcher().fetchImageFromUrl(diagramId)
    if (image == null) {
      UserFeedback(fragment.activity).showMessage(R.string.message_diagram_update_failed)
      val message = "Fetching Image $diagramId failed"
      Log.e(DiagramManager::class.java.name, message)
      throw RuntimeException(message)
    }
    return image
  }

  private fun showDiagram(diagram: Diagram) {
    val newImage = diagram.image
    showDrawable(newImage)
  }

  private fun showDrawable(newImage: Drawable) {
    if (active) {
      fragment.activity.runOnUiThread {
        val view = fragment.view
        if (view != null) {
          val imageView = view.findViewById(R.id.diagram_view) as ImageView
          imageView.setImageDrawable(newImage)
          val attacher = PhotoViewAttacher(imageView)
          attacher.update()
        } else {
          Log.e("DiagramManager", "View is null")
        }
      }
    }
  }

}