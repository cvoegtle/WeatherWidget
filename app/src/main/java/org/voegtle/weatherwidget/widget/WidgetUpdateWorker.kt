package org.voegtle.weatherwidget.widget

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import org.voegtle.weatherwidget.cache.WeatherDataCache
import org.voegtle.weatherwidget.data.LocationDataSet
import org.voegtle.weatherwidget.location.LocationDataSetFactory
import org.voegtle.weatherwidget.location.LocationSorter
import org.voegtle.weatherwidget.notification.NotificationSystemManager
import org.voegtle.weatherwidget.preferences.ApplicationPreferences
import org.voegtle.weatherwidget.preferences.WeatherPreferencesReader
import org.voegtle.weatherwidget.util.ContextUtil
import org.voegtle.weatherwidget.util.FetchAllResponse
import org.voegtle.weatherwidget.util.WeatherDataFetcher
import org.voegtle.weatherwidget.watch.WatchDataStore

class WidgetUpdateWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    private val configuration: ApplicationPreferences
    private val weatherDataFetcher = WeatherDataFetcher(ContextUtil.getBuildNumber(appContext))
    private val locationDataSetFactory = LocationDataSetFactory(applicationContext)
    private val locationSorter = LocationSorter(applicationContext)
    private val weatherDataCache = WeatherDataCache(applicationContext)
    private val watchDataStore: WatchDataStore


    init {
        val weatherPreferencesReader = WeatherPreferencesReader(applicationContext)
        configuration = weatherPreferencesReader.read()
        watchDataStore = WatchDataStore(applicationContext, configuration)
    }

    override suspend fun doWork(): Result {
        try {
            val response = fetchWeatherDataFromServer()
            if (response.valid) {
                updatedCache(response)
                updateWatch(response)

                val locationDataSets = convertToSortedLocationDataSets(response)
                updateWeatherWidgetState(applicationContext, locationDataSets)
                updateNotification(response)
            } else {
                Log.e(WidgetUpdateWorker::class.java.toString(), "Failed to fetch weather data in background")
            }
        } catch (th: Throwable) {
            Log.e(WidgetUpdateWorker::class.java.toString(), "Failed to update widget in background", th)
            return Result.failure()
        }

        return Result.success()
    }

    private fun updatedCache(response: FetchAllResponse) {
        weatherDataCache.write(response)
    }

    private fun updateWatch(response: FetchAllResponse) {
        if (response.valid) {
            watchDataStore.sendWeatherData(response.weatherMap)
        }
    }

    private fun convertToSortedLocationDataSets(response: FetchAllResponse): List<LocationDataSet> {
        val locationDataSets = locationDataSetFactory.assembleLocationDataSets(configuration.locations, response.weatherMap)
        locationSorter.sort(locationDataSets)
        return locationDataSets
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