package org.voegtle.weatherwidget.widget

import android.appwidget.AppWidgetManager
import android.widget.RemoteViews
import org.voegtle.weatherwidget.R
import org.voegtle.weatherwidget.data.WeatherData
import org.voegtle.weatherwidget.preferences.ColorScheme
import org.voegtle.weatherwidget.util.ColorUtil

class SmallWidgetScreenPainter(appWidgetManager: AppWidgetManager, widgetIds: IntArray, private val remoteViews: RemoteViews) : AbstractWidgetScreenPainter(appWidgetManager, widgetIds, remoteViews) {

  override fun showDataIsInvalid() {
    remoteViews.setTextColor(R.id.weather_small, ColorUtil.updateColor(ColorScheme.dark))
  }

  override fun showDataIsValid() {
    updateAllWidgets()
  }

  fun updateData(data: WeatherData?) {
    if (data != null) {
      remoteViews.setTextViewText(R.id.weather_small, formatter.formatTemperature(data))
      remoteViews.setTextColor(R.id.weather_small, ColorUtil.byAge(data.timestamp!!))
    } else {
      remoteViews.setTextColor(R.id.weather_small, ColorUtil.outdatedColor(ColorScheme.dark))
    }
  }
}
