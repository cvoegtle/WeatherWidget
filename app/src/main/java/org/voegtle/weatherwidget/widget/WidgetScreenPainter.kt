package org.voegtle.weatherwidget.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.RemoteViews
import org.voegtle.weatherwidget.R
import org.voegtle.weatherwidget.data.WeatherData
import org.voegtle.weatherwidget.location.LocationIdentifier
import org.voegtle.weatherwidget.location.LocationSorter
import org.voegtle.weatherwidget.location.WeatherLocation
import org.voegtle.weatherwidget.preferences.ApplicationSettings
import org.voegtle.weatherwidget.util.ColorUtil
import org.voegtle.weatherwidget.util.DateUtil
import org.voegtle.weatherwidget.widget.view.ViewId
import org.voegtle.weatherwidget.widget.view.ViewIdFactory
import java.util.Date

class WidgetScreenPainter(appWidgetManager: AppWidgetManager,
                          widgetIds: IntArray,
                          private val remoteViews: RemoteViews,
                          context: Context,
                          val configuration: ApplicationSettings,
                          refreshImage: Drawable,
                          private val detailed: Boolean) : AbstractWidgetScreenPainter(appWidgetManager, widgetIds,
                                                                                       remoteViews) {
  private val colorScheme = configuration.colorScheme
  private val viewIds = ViewIdFactory.buildViewIds()
  private val locationSorter = LocationSorter(context)


  override fun showDataIsInvalid() {
    viewIds.forEach { viewIds ->
      remoteViews.setTextColor(viewIds.rain, ColorUtil.updateColor(colorScheme))
      remoteViews.setTextColor(viewIds.weather, ColorUtil.updateColor(colorScheme))
    }
    remoteViews.setTextColor(R.id.update_time, ColorUtil.updateColor(colorScheme))
    updateAllWidgets()
  }

  override fun showDataIsValid() {
    updateAllWidgets()
  }

  fun updateWidgetData(data: HashMap<LocationIdentifier, WeatherData>) {
    val sortedData = locationSorter.sort(data)
    val relevantData = reduceToWidgetData(sortedData)
    for (i in viewIds.indices) {
      if (i < relevantData.size) {
        val location = configuration.findLocation(relevantData[i].location)
        location?.let {
          visualizeData(it, relevantData[i], viewIds[i])
        }
      } else {
        hideLine(viewIds[i])
      }
    }
    if (relevantData.isNotEmpty()) {
      updateUpdateTime()
    }
  }

  private fun updateUpdateTime() {
    remoteViews.setTextViewText(R.id.update_time, DateUtil.currentTime)
    remoteViews.setTextColor(R.id.update_time, ColorUtil.byAge(colorScheme, Date()))
  }

  private fun visualizeData(location: WeatherLocation, data: WeatherData, viewId: ViewId) {
    var textColor = ColorUtil.byAge(colorScheme, data.timestamp)
    if (location.preferences.favorite && !DateUtil.isOutdated(data.timestamp)) {
      textColor = ColorUtil.highlightText(colorScheme)
    }
    remoteViews.setTextColor(viewId.weather, textColor)
    remoteViews.setTextViewText(viewId.weather, formatter.formatWidgetLine(location, data, detailed))

    remoteViews.setTextColor(viewId.rain, ColorUtil.byRain(data.isRaining, colorScheme, data.timestamp))
    remoteViews.setViewVisibility(viewId.line, View.VISIBLE)
  }

  private fun hideLine(viewId: ViewId) {
    remoteViews.setViewVisibility(viewId.line, View.GONE)
  }

  private fun reduceToWidgetData(sortedData: List<WeatherData>): List<WeatherData> {
    val relevantData = ArrayList<WeatherData>()

    sortedData.forEach {
      val location = configuration.findLocation(it.location)
      if (location != null && location.preferences.showInWidget) {
        relevantData.add(it)
      }
    }

    return relevantData;
  }

}
