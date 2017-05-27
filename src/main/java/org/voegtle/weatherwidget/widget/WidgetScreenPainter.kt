package org.voegtle.weatherwidget.widget

import android.appwidget.AppWidgetManager
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.RemoteViews
import org.voegtle.weatherwidget.R
import org.voegtle.weatherwidget.data.WeatherData
import org.voegtle.weatherwidget.location.LocationIdentifier
import org.voegtle.weatherwidget.location.WeatherLocation
import org.voegtle.weatherwidget.preferences.ApplicationSettings
import org.voegtle.weatherwidget.preferences.ColorScheme
import org.voegtle.weatherwidget.util.ColorUtil

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.HashMap
import java.util.Locale

class WidgetScreenPainter(appWidgetManager: AppWidgetManager, widgetIds: IntArray, private val remoteViews: RemoteViews, configuration: ApplicationSettings, refreshImage: Drawable, private val detailed: Boolean) : AbstractWidgetScreenPainter(appWidgetManager, widgetIds, remoteViews) {
  private val colorScheme: ColorScheme
  private val locations: List<WeatherLocation>

  init {
    this.locations = configuration.locations
    this.colorScheme = configuration.colorScheme
    remoteViews.setBitmap(R.id.refresh_button, "setImageBitmap", (refreshImage as BitmapDrawable).bitmap)
  }

  override fun showDataIsInvalid() {
    remoteViews.setViewVisibility(R.id.refresh_button, View.GONE)

    for (location in locations) {
      remoteViews.setTextColor(location.weatherViewId, ColorUtil.updateColor(colorScheme))
      remoteViews.setTextColor(location.rainIndicatorId, ColorUtil.updateColor(colorScheme))
    }
    remoteViews.setTextColor(R.id.update_time, ColorUtil.updateColor(colorScheme))
    updateAllWidgets()
  }

  override fun showDataIsValid() {
    remoteViews.setViewVisibility(R.id.refresh_button, View.VISIBLE)
    updateAllWidgets()
  }

  fun updateWidgetData(data: HashMap<LocationIdentifier, WeatherData>) {
    var updated = false
    for (location in locations) {
      val weatherData = data[location.key]
      updated = updated or visualizeData(location, weatherData)
    }
    if (updated) {
      updateUpdateTime()
    }
  }

  private fun updateUpdateTime() {
    val df = SimpleDateFormat("HH:mm", Locale.GERMANY)
    remoteViews.setTextViewText(R.id.update_time, df.format(Date()))
    remoteViews.setTextColor(R.id.update_time, ColorUtil.byAge(colorScheme, Date()))
  }

  private fun visualizeData(location: WeatherLocation, data: WeatherData?): Boolean {
    val updated: Boolean
    if (data == null) {
      remoteViews.setTextColor(location.weatherViewId, ColorUtil.outdatedColor(colorScheme))
      remoteViews.setTextColor(location.rainIndicatorId, ColorUtil.outdatedColor(colorScheme))
      updated = false
    } else {
      remoteViews.setTextColor(location.weatherViewId, ColorUtil.byAge(colorScheme, data.timestamp!!))
      remoteViews.setTextViewText(location.weatherViewId, formatter.formatWidgetLine(location, data, detailed))

      remoteViews.setTextColor(location.rainIndicatorId, ColorUtil.byRain(data.isRaining, colorScheme, data.timestamp!!))
      updated = true
    }
    return updated
  }

}
