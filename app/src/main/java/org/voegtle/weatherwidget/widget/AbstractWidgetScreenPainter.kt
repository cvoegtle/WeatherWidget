package org.voegtle.weatherwidget.widget

import android.appwidget.AppWidgetManager
import android.widget.RemoteViews
import org.voegtle.weatherwidget.util.DataFormatter

abstract class AbstractWidgetScreenPainter protected constructor(private val appWidgetManager: AppWidgetManager, private val widgetIds: IntArray, private val remoteViews: RemoteViews) {
  protected var formatter = DataFormatter()

  abstract fun showDataIsInvalid()

  abstract fun showDataIsValid()

  fun updateAllWidgets() {
    appWidgetManager.updateAppWidget(widgetIds, remoteViews)
  }

}
