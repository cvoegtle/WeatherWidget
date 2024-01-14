package org.voegtle.weatherwidget.widget

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build.VERSION.SDK_INT
import android.util.TypedValue
import android.view.View
import android.widget.RemoteViews
import androidx.core.content.ContextCompat
import org.voegtle.weatherwidget.R
import org.voegtle.weatherwidget.WeatherWidgetProvider
import org.voegtle.weatherwidget.WeatherWidgetProviderLarge
import org.voegtle.weatherwidget.preferences.ApplicationSettings
import org.voegtle.weatherwidget.preferences.ColorScheme
import org.voegtle.weatherwidget.system.IntentFactory
import org.voegtle.weatherwidget.widget.view.ViewIdFactory

class ScreenPainterFactory(context: Context, private val configuration: ApplicationSettings) {

  private val context = context.applicationContext
  private val viewIds = ViewIdFactory.buildViewIds()

  fun createScreenPainters(): ArrayList<WidgetScreenPainter> {
    val screenPainters = ArrayList<WidgetScreenPainter>()
    getWidgetScreenPainter(screenPainters, false, WeatherWidgetProvider::class.java)
    getWidgetScreenPainter(screenPainters, true, WeatherWidgetProviderLarge::class.java)
    return screenPainters
  }

  private fun getWidgetScreenPainter(screenPainters: ArrayList<WidgetScreenPainter>, isDetailed: Boolean,
                                     clazz: Class<*>) {
    val thisWidget = ComponentName(context, clazz)
    val appWidgetManager = AppWidgetManager.getInstance(context)
    val allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget)
    if (allWidgetIds.isNotEmpty()) {
      screenPainters.add(WidgetScreenPainter(appWidgetManager, allWidgetIds, createRemoteViews(), context,
                                             configuration, refreshImage, isDetailed))
    }
  }

  private val refreshImage: Drawable
    get() = ContextCompat.getDrawable(context,
                                      if (configuration.colorScheme === ColorScheme.dark) R.drawable.ic_action_refresh else R.drawable.ic_action_refresh_dark)!!

  private fun createRemoteViews(): RemoteViews {
    val remoteViews = RemoteViews(context.packageName, R.layout.widget_weather)

    updateBackgroundColor(remoteViews)
    viewIds.forEach { viewId ->
      if (SDK_INT >= 16) {
        remoteViews.setTextViewTextSize(viewId.weather, TypedValue.COMPLEX_UNIT_SP,
                                        configuration.widgetTextSize.toFloat())
      }
    }

    setWidgetIntents(remoteViews)
    return remoteViews
  }

  private fun updateVisibility(remoteViews: RemoteViews, id: Int, isVisible: Boolean) {
    remoteViews.setViewVisibility(id, if (isVisible) View.VISIBLE else View.GONE)
  }


  private fun updateBackgroundColor(remoteViews: RemoteViews) {
    if (configuration.colorScheme == ColorScheme.dark) {
      remoteViews.setInt(R.id.widget_container, "setBackgroundColor", Color.argb(0xB1, 0x00, 0x00, 0x00))
    } else {
      remoteViews.setInt(R.id.widget_container, "setBackgroundColor", Color.argb(0xD0, 0xff, 0xff, 0xff))
    }
  }


  private fun setWidgetIntents(remoteViews: RemoteViews) {
    remoteViews.setOnClickPendingIntent(R.id.widget_container,
                                        IntentFactory.createRefreshIntent(context))
  }


}
