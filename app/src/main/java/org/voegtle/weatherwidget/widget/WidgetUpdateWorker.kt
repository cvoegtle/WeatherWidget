package org.voegtle.weatherwidget.widget

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import org.voegtle.weatherwidget.location.LocationDataSetFactory
import org.voegtle.weatherwidget.location.LocationSorter
import org.voegtle.weatherwidget.notification.NotificationSystemManager
import org.voegtle.weatherwidget.preferences.ApplicationSettings
import org.voegtle.weatherwidget.preferences.WeatherSettingsReader
import org.voegtle.weatherwidget.util.ContextUtil
import org.voegtle.weatherwidget.util.FetchAllResponse
import org.voegtle.weatherwidget.util.WeatherDataFetcher

class WidgetUpdateWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    private val configuration: ApplicationSettings
    private val weatherDataFetcher = WeatherDataFetcher(ContextUtil.getBuildNumber(appContext))
    private val locationDataSetFactory = LocationDataSetFactory(applicationContext)
    private val locationSorter = LocationSorter(applicationContext)

    init {
        val preferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val weatherSettingsReader = WeatherSettingsReader(applicationContext.resources)
        configuration = weatherSettingsReader.read(preferences)
    }

    override suspend fun doWork(): Result {
        try {
            val response = fetchWeatherDataFromServer()
            if (response.valid) {
                // Update the Glance widget state
                val locationDataSets = locationDataSetFactory.assembleLocationDataSets(configuration.locations, response.weatherMap)
                locationSorter.sort(locationDataSets)

                updateWeatherWidgetState(applicationContext, locationDataSets)

                // Update notifications (optional, but good to keep)
                updateNotification(response)
            } else {
                // Optionally, you could update the widget to show an error state
                Log.e(WidgetUpdateWorker::class.java.toString(), "Failed to fetch weather data in background")
            }
        } catch (th: Throwable) {
            Log.e(WidgetUpdateWorker::class.java.toString(), "Failed to update widget in background", th)
            return Result.failure()
        }

        return Result.success()
    }

    private fun fetchWeatherDataFromServer(): FetchAllResponse {
        if (configuration.locations.isEmpty() || configuration.secret.isNullOrEmpty()) {
            return FetchAllResponse(true, HashMap())
        }
        return weatherDataFetcher.fetchAllWeatherDataFromServer(configuration.locations, configuration.secret!!)
    }

    private fun updateNotification(data: FetchAllResponse) {
        val notificationManager = NotificationSystemManager(applicationContext, configuration)
        notificationManager.updateNotification(data)
    }
}