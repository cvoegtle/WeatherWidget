package org.voegtle.weatherwidget.util

import android.content.Context
import android.content.res.Resources
import android.preference.PreferenceManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.google.gson.Gson
import org.voegtle.weatherwidget.location.UserLocationUpdater
import org.voegtle.weatherwidget.preferences.ApplicationSettings
import org.voegtle.weatherwidget.preferences.WeatherSettingsReader

class ActivityUpdateWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {
  companion object {
    const val WEATHER_DATA = "ACTIVITY_RESULT"
  }

  private var res: Resources = applicationContext.resources

  private val weatherDataFetcher = WeatherDataFetcher(ContextUtil.getBuildNumber(appContext))

  private val configuration: ApplicationSettings

  init {
    val preferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)

    val weatherSettingsReader = WeatherSettingsReader(res)
    configuration = weatherSettingsReader.read(preferences)
  }

  override fun doWork(): Result {
    val weatherData = fetchWeatherData()
    val workResult = workDataOf(WEATHER_DATA to Gson().toJson(weatherData))
    return Result.success(workResult)
  }

  private fun fetchWeatherData(): FetchAllResponse {
    return weatherDataFetcher.fetchAllWeatherDataFromServer(configuration.locations, configuration.secret!!)
  }

}
