package org.voegtle.weatherwidget.util

import android.content.Context
import androidx.work.WorkerParameters
import org.voegtle.weatherwidget.cache.WeatherDataCache
import org.voegtle.weatherwidget.watch.WatchDataPusher

class WeatherDataUpdateWorker(appContext: Context, workerParams: WorkerParameters) : UpdateWorker(appContext, workerParams) {
  companion object {
    const val WEATHER_DATA = "WEATHER_DATA"
  }

  val weatherDataCache = WeatherDataCache(appContext)
  val watchDataPusher: WatchDataPusher = WatchDataPusher(applicationContext, configuration)

    override fun doWork(): Result {
    val weatherData = fetchWeatherData()
    updateCache(weatherData)
    updateWatch(weatherData)
    return Result.success()
  }

  private fun updateCache(weatherData: FetchAllResponse) {
    weatherDataCache.write(weatherData)
  }

  private fun updateWatch(weatherData: FetchAllResponse) {
    if (weatherData.valid) {
      watchDataPusher.sendWeatherData(weatherData.weatherMap)
    }
  }

  private fun fetchWeatherData(): FetchAllResponse {
    return weatherDataFetcher.fetchAllWeatherDataFromServer(configuration.locations, configuration.secret!!)
  }

}
