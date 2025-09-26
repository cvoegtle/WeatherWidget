package org.voegtle.weatherwidget.util

import android.content.Context
import android.content.res.Resources
import android.preference.PreferenceManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.google.gson.Gson
import org.voegtle.weatherwidget.cache.WeatherDataCache
import org.voegtle.weatherwidget.preferences.ApplicationSettings
import org.voegtle.weatherwidget.preferences.WeatherSettingsReader

class WeatherDataUpdateWorker(appContext: Context, workerParams: WorkerParameters) : UpdateWorker(appContext, workerParams) {
  companion object {
    const val WEATHER_DATA = "WEATHER_DATA"
  }

  val weatherDataCache = WeatherDataCache(appContext)

  override fun doWork(): Result {
    val weatherData = fetchWeatherData()
    weatherDataCache.write(Gson().toJson(weatherData))
    return Result.success()
  }

  private fun fetchWeatherData(): FetchAllResponse {
    return weatherDataFetcher.fetchAllWeatherDataFromServer(configuration.locations, configuration.secret!!)
  }

}
