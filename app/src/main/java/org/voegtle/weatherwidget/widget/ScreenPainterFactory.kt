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
import org.voegtle.weatherwidget.data.WeatherData
import org.voegtle.weatherwidget.location.LocationIdentifier
import org.voegtle.weatherwidget.preferences.ApplicationSettings
import org.voegtle.weatherwidget.system.IntentFactory
import org.voegtle.weatherwidget.widget.view.ViewIdFactory

class ScreenPainterFactory(context: Context, private val configuration: ApplicationSettings) {

    private val context = context.applicationContext

    fun createScreenPainters(): ArrayList<WidgetScreenPainter> {
        val screenPainters = ArrayList<WidgetScreenPainter>()
        getWidgetScreenPainter(screenPainters, false, WeatherWidgetProvider::class.java)
        getWidgetScreenPainter(screenPainters, true, WeatherWidgetProviderLarge::class.java)
        return screenPainters
    }

    private fun getWidgetScreenPainter(
        screenPainters: ArrayList<WidgetScreenPainter>, isDetailed: Boolean,
        clazz: Class<*>
    ) {
        val thisWidget = ComponentName(context, clazz)
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget)
        if (allWidgetIds.isNotEmpty()) {
            screenPainters.add(
                WidgetScreenPainter(
                    appWidgetManager, allWidgetIds, createRemoteViews(), context,
                    configuration, isDetailed
                )
            )
        }
    }

    private fun createRemoteViews(): RemoteViews {
        val remoteViews = RemoteViews(context.packageName, R.layout.widget_weather)

        updateBackgroundColor(remoteViews)

        setWidgetIntents(remoteViews)
        return remoteViews
    }

    private fun updateBackgroundColor(remoteViews: RemoteViews) {
        remoteViews.setInt(R.id.widget_container, "setBackgroundColor", Color.argb(0xD0, 0xff, 0xff, 0xff))
    }


    private fun setWidgetIntents(remoteViews: RemoteViews) {
        remoteViews.setOnClickPendingIntent(
            R.id.widget_container,
            IntentFactory.createRefreshIntent(context)
        )
    }

    fun createWeatherLines(data: HashMap<LocationIdentifier, WeatherData>): List<String> {
        val lines = mutableListOf<String>()
        // TODO: Implement the logic to convert the weather data into a list of strings.
        // For example, you could iterate through the data and format it.
        data.forEach { (location, weather) ->
            val line = "${weather.location_short}: ${weather.temperature}°C" // Example formatting
            lines.add(line)
        }
        return lines
    }

}
