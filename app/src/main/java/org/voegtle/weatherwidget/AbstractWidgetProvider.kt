package org.voegtle.weatherwidget

import android.app.AlarmManager
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Resources
import android.os.Build
import android.preference.PreferenceManager
import android.widget.RemoteViews
import org.voegtle.weatherwidget.preferences.ApplicationSettings
import org.voegtle.weatherwidget.preferences.WeatherSettingsReader
import org.voegtle.weatherwidget.system.IntentFactory
import org.voegtle.weatherwidget.widget.WidgetRefreshService

abstract class AbstractWidgetProvider : AppWidgetProvider() {

    private var configuration: ApplicationSettings? = null
    private var res: Resources? = null
    private var remoteViews: RemoteViews? = null

    override fun onEnabled(context: Context) {
        ensureResources(context)
        if (Build.VERSION.SDK_INT >= 26) {
            context.startForegroundService(Intent(context, WidgetRefreshService::class.java))
        } else {
            val alarmManager: AlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.set(
                AlarmManager.RTC, System.currentTimeMillis() + 10,
                IntentFactory.createRefreshIntent(context, WidgetRefreshService::class.java)
            )
        }

        super.onEnabled(context)
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        ensureResources(context)

        appWidgetIds.forEach { widgetId ->
            val pendingOpenApp = IntentFactory.createOpenAppIntent(context.applicationContext)
            configuration?.let {
                it.locations.forEach { location ->
                    remoteViews?.setOnClickPendingIntent(location.weatherViewId, pendingOpenApp)
                }
                val intent = IntentFactory.createRefreshIntent(context.applicationContext, WidgetRefreshService::class.java)
                remoteViews?.setOnClickPendingIntent(R.id.refresh_button, intent)
                appWidgetManager.updateAppWidget(widgetId, remoteViews)
            }
        }
    }

    override fun onDisabled(context: Context) {
        ensureResources(context)
    }

    private fun ensureResources(context: Context) {
        if (res == null) {
            val appContext = context.applicationContext
            this.res = appContext.resources

            this.remoteViews = RemoteViews(appContext.packageName, R.layout.widget_weather)

            val preferences = PreferenceManager.getDefaultSharedPreferences(appContext)
            processPreferences(preferences, appContext)
        }
    }

    private fun processPreferences(preferences: SharedPreferences, context: Context) {
        val weatherSettingsReader = WeatherSettingsReader(context)
        configuration = weatherSettingsReader.read(preferences)
    }
}
