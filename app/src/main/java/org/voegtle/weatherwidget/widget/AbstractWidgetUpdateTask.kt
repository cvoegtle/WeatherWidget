package org.voegtle.weatherwidget.widget

import android.content.Context
import android.os.AsyncTask
import org.voegtle.weatherwidget.data.WeatherData
import org.voegtle.weatherwidget.notification.NotificationSystemManager
import org.voegtle.weatherwidget.preferences.ApplicationSettings
import org.voegtle.weatherwidget.util.ContextUtil
import org.voegtle.weatherwidget.util.FetchAllResponse
import org.voegtle.weatherwidget.util.WeatherDataFetcher

abstract class AbstractWidgetUpdateTask<Params, Progress, Result>(private val context: Context,
                                                                  protected var configuration: ApplicationSettings) : AsyncTask<Params, Progress, Result>() {
  protected val weatherDataFetcher = WeatherDataFetcher(ContextUtil.getBuildNumber(context))

  protected fun fetchWeatherData(weatherServerUrl: String): WeatherData? {
    return weatherDataFetcher.fetchWeatherDataFromUrl(weatherServerUrl)
  }

  protected fun updateNotification(data: FetchAllResponse) {
    val notificationManager = NotificationSystemManager(context, configuration)
    notificationManager.updateNotification(data)
  }


}
