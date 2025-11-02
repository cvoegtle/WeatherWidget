package org.voegtle.weatherwidget.util

import android.content.Context
import androidx.work.WorkerParameters
import com.google.gson.Gson
import org.voegtle.weatherwidget.cache.WeatherDataCache

class WeatherDataUpdateWorker(appContext: Context, workerParams: WorkerParameters) : UpdateWorker(appContext, workerParams) {
  companion object {
    const val WEATHER_DATA = "WEATHER_DATA"
  }

  val weatherDataCache = WeatherDataCache(appContext)

  override fun doWork(): Result {
    val weatherData = fetchWeatherData()
    weatherDataCache.write(weatherData)
    return Result.success()
  }

  private fun fetchWeatherData(): FetchAllResponse {
    return weatherDataFetcher.fetchAllWeatherDataFromServer(configuration.locations, configuration.secret!!)
  }

}
