package org.voegtle.weatherwidget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Resources
import android.preference.PreferenceManager
import android.widget.RemoteViews
import org.voegtle.weatherwidget.preferences.ApplicationSettings
import org.voegtle.weatherwidget.preferences.WeatherSettingsReader
import org.voegtle.weatherwidget.widget.SmallWidgetScreenPainter
import org.voegtle.weatherwidget.widget.SmallWidgetUpdateTask

open class WeatherWidgetSmallProvider(private val weatherDataUrl: String, private val resourceKeyCity: Int) : AppWidgetProvider() {

  override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
    val res = context.resources
    val remoteViews = RemoteViews(context.packageName, R.layout.widget_small_weather)
    remoteViews.setTextViewText(R.id.weather_location, res.getString(resourceKeyCity))

    val screenPainter = SmallWidgetScreenPainter(appWidgetManager, appWidgetIds, remoteViews)

    val preferences = PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
    val weatherSettingsReader = WeatherSettingsReader(res)
    val configuration = weatherSettingsReader.read(preferences)


    SmallWidgetUpdateTask(context, configuration, screenPainter).execute(weatherDataUrl)

    appWidgetIds.forEach { widgetId ->
      val intentRefresh = Intent(context, this.javaClass)
      intentRefresh.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
      intentRefresh.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds)
      val pendingRefresh = PendingIntent.getBroadcast(context, 0, intentRefresh, PendingIntent.FLAG_UPDATE_CURRENT)

      remoteViews.setOnClickPendingIntent(R.id.weather_small, pendingRefresh)

      appWidgetManager.updateAppWidget(widgetId, remoteViews)
    }

    super.onUpdate(context, appWidgetManager, appWidgetIds)
  }


}
