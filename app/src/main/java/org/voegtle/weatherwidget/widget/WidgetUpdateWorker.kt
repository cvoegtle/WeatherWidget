package org.voegtle.weatherwidget.widget

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import android.preference.PreferenceManager
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import org.voegtle.weatherwidget.R
import org.voegtle.weatherwidget.data.WeatherData
import org.voegtle.weatherwidget.location.LocationIdentifier
import org.voegtle.weatherwidget.notification.NotificationSystemManager
import org.voegtle.weatherwidget.preferences.ApplicationSettings
import org.voegtle.weatherwidget.preferences.WeatherSettingsReader
import org.voegtle.weatherwidget.util.ContextUtil
import org.voegtle.weatherwidget.util.FetchAllResponse
import org.voegtle.weatherwidget.util.UserFeedback
import org.voegtle.weatherwidget.util.WeatherDataFetcher

class WidgetUpdateWorker(appContext: Context, workerParams: WorkerParameters):
    Worker(appContext, workerParams) {
    private var res: Resources = applicationContext.resources

    private val configuration: ApplicationSettings
    private val screenPainters: List<WidgetScreenPainter>
    private val weatherDataFetcher = WeatherDataFetcher(ContextUtil.getBuildNumber(appContext))

    init {
        val preferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)

        val weatherSettingsReader = WeatherSettingsReader(res)
        configuration = weatherSettingsReader.read(preferences)
        screenPainters = ScreenPainterFactory(applicationContext, configuration).createScreenPainters()
    }

    override fun doWork(): Result {
        cleanup()
        val response = fetchWeatherData()
        refreshLocationData(response.weatherMap)
        updateWidgets(response)

        return Result.success()
    }

    fun cleanup() {
        screenPainters.forEach { screenPainter ->
            screenPainter.showDataIsInvalid()
        }
    }

    fun fetchWeatherData(): FetchAllResponse {
        if (!screenPainters.isEmpty()) {
            return fetchWeatherDataFromServer()
        }

        return FetchAllResponse(true, HashMap())
    }

    private fun fetchWeatherDataFromServer(): FetchAllResponse {
        return weatherDataFetcher.fetchAllWeatherDataFromServer(configuration.locations, configuration.secret!!)
    }

    fun updateWidgets(response: FetchAllResponse) {
        try {
            if (response.valid) {
                screenPainters.forEach { screenPainter -> screenPainter.updateWidgetData(response.weatherMap) }
                updateNotification(response)
            } else {
                UserFeedback(applicationContext).showMessage(R.string.message_data_update_failed, true)
            }
        } catch (th: Throwable) {
            Log.e(WidgetUpdateWorker::class.java.toString(), "Failed to update View", th)
        } finally {
            showDataIsValid()
        }
    }

    private fun refreshLocationData(data: java.util.HashMap<LocationIdentifier, WeatherData>) {
        configuration!!.locations.forEach { location ->
            data[location.key]?.let {
                location.refresh(it)
            }
        }
    }

    private fun showDataIsValid() {
        try {
            screenPainters.forEach { screenPainter -> screenPainter.showDataIsValid() }
        } catch (th: Throwable) {
            Log.e(WidgetUpdateWorker::class.java.toString(), "Failed to repaint view", th)
        }
    }

    protected fun updateNotification(data: FetchAllResponse) {
        val notificationManager = NotificationSystemManager(applicationContext, configuration)
        notificationManager.updateNotification(data)
    }

}