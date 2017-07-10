package org.voegtle.weatherwidget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import android.preference.PreferenceManager
import android.widget.RemoteViews
import org.voegtle.weatherwidget.location.WeatherLocation
import org.voegtle.weatherwidget.preferences.ApplicationSettings
import org.voegtle.weatherwidget.preferences.WeatherSettingsReader
import org.voegtle.weatherwidget.system.AbstractWidgetUpdateManager
import org.voegtle.weatherwidget.system.IntentFactory

abstract class AbstractWidgetProvider : AppWidgetProvider() {

  private var configuration: ApplicationSettings? = null
  private var res: Resources? = null
  private var remoteViews: RemoteViews? = null
  private var updateManager: AbstractWidgetUpdateManager? = null

  internal abstract fun getWidgetServiceClass(): Class<*>

  internal abstract fun getUpdateManager(context: Context): AbstractWidgetUpdateManager

  override fun onEnabled(context: Context) {
    ensureResources(context)

    updateManager!!.rescheduleService()
    super.onEnabled(context)
  }

  override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
    ensureResources(context)

    appWidgetIds.forEach { widgetId ->
      val pendingOpenApp = IntentFactory.createOpenAppIntent(context.applicationContext)
      configuration?.locations?.forEach { location -> remoteViews!!.setOnClickPendingIntent(location.weatherViewId, pendingOpenApp) }

      remoteViews!!.setOnClickPendingIntent(R.id.refresh_button,
          IntentFactory.createRefreshIntent(context.applicationContext, getWidgetServiceClass()))

      appWidgetManager.updateAppWidget(widgetId, remoteViews)
    }
  }

  override fun onDisabled(context: Context) {
    ensureResources(context)
    updateManager?.cancelAlarmService()
  }

  private fun ensureResources(context: Context) {
    if (res == null) {
      val appContext = context.applicationContext
      this.res = appContext.resources

      this.remoteViews = RemoteViews(appContext.packageName, R.layout.widget_weather)

      this.updateManager = getUpdateManager(appContext)

      val preferences = PreferenceManager.getDefaultSharedPreferences(appContext)
      processPreferences(preferences, appContext)
    }
  }

  private fun processPreferences(preferences: SharedPreferences, context: Context) {
    val weatherSettingsReader = WeatherSettingsReader(context)
    configuration = weatherSettingsReader.read(preferences)
  }

}
