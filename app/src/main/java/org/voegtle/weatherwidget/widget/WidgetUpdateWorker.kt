package org.voegtle.weatherwidget.widget

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import org.voegtle.weatherwidget.location.LocationDataSetFactory
import org.voegtle.weatherwidget.location.LocationSorter
import org.voegtle.weatherwidget.notification.NotificationSystemManager
import org.voegtle.weatherwidget.preferences.ApplicationPreferences
import org.voegtle.weatherwidget.preferences.WeatherPreferencesReader
import org.voegtle.weatherwidget.util.ContextUtil
import org.voegtle.weatherwidget.util.FetchAllResponse
import org.voegtle.weatherwidget.util.WeatherDataFetcher

class WidgetUpdateWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    private val configuration: ApplicationPreferences
    private val weatherDataFetcher = WeatherDataFetcher(ContextUtil.getBuildNumber(appContext))
    private val locationDataSetFactory = LocationDataSetFactory(applicationContext)
    private val locationSorter = LocationSorter(applicationContext)

    init {
        val weatherPreferencesReader = WeatherPreferencesReader(applicationContext)
        configuration = weatherPreferencesReader.read()
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
        if (configuration.locations.isEmpty()) {
            return FetchAllResponse(true, HashMap())
        }
        return weatherDataFetcher.fetchAllWeatherDataFromServer(configuration.locations, configuration.secret!!)
    }

    private fun updateNotification(data: FetchAllResponse) {
        val notificationManager = NotificationSystemManager(applicationContext, configuration)
        notificationManager.updateNotification(data)
    }
}