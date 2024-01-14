package org.voegtle.weatherwidget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import android.preference.PreferenceManager
import android.widget.RemoteViews
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import org.voegtle.weatherwidget.preferences.ApplicationSettings
import org.voegtle.weatherwidget.preferences.WeatherSettingsReader
import org.voegtle.weatherwidget.system.IntentFactory
import org.voegtle.weatherwidget.widget.WidgetUpdateWorker
import java.util.concurrent.TimeUnit

private const val UPDATE_WORK = "update-widget"

abstract class AbstractWidgetProvider : AppWidgetProvider() {

    private var configuration: ApplicationSettings? = null
    private var res: Resources? = null
    private var remoteViews: RemoteViews? = null

    override fun onEnabled(context: Context) {
        ensureResources(context)
        enqueueInitialWidgetUpdateWorker(context)
        enqueuePeriodicWidgetUpdateWorker(context)
        super.onEnabled(context)
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        ensureResources(context)

        appWidgetIds.forEach { widgetId ->
            configuration?.let {
                val intent = IntentFactory.createRefreshIntent(context.applicationContext)
                remoteViews?.setOnClickPendingIntent(R.id.widget_container, intent)
                appWidgetManager.updateAppWidget(widgetId, remoteViews)
            }
        }
    }

    private fun enqueueInitialWidgetUpdateWorker(context: Context) {
        val widgetUpdateRequest = OneTimeWorkRequestBuilder<WidgetUpdateWorker>().setInitialDelay(5, TimeUnit.SECONDS).build()
        WorkManager.getInstance(context).enqueue(widgetUpdateRequest)
    }

    private fun enqueuePeriodicWidgetUpdateWorker(context: Context) {
        val widgetUpdateRequest = PeriodicWorkRequestBuilder<WidgetUpdateWorker>(16, TimeUnit.MINUTES).build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(UPDATE_WORK, ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE, widgetUpdateRequest)
    }

    override fun onDisabled(context: Context) {
        ensureResources(context)
        WorkManager.getInstance(context).cancelUniqueWork(UPDATE_WORK)
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
