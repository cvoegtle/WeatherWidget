package org.voegtle.weatherwidget.util

import org.voegtle.weatherwidget.WeatherActivity
import org.voegtle.weatherwidget.preferences.ApplicationSettings

import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

class WeatherDataUpdater(private val activity: WeatherActivity, private val configuration: ApplicationSettings?) {
  private var backgroundProcess: ScheduledFuture<*>? = null

  fun stopWeatherScheduler() {
    if (backgroundProcess != null) {
      backgroundProcess!!.cancel(true)
      backgroundProcess = null
    }
  }

  fun startWeatherScheduler(interval: Int) {
    stopWeatherScheduler()

    val updater = Runnable { ActivityUpdateTask(activity, configuration!!, false).execute() }
    val scheduler = Executors.newScheduledThreadPool(1)
    backgroundProcess = scheduler.scheduleAtFixedRate(updater, interval.toLong(), interval.toLong(), TimeUnit.SECONDS)
  }

  fun updateWeatherOnce(showToast: Boolean) {
    ActivityUpdateTask(activity, configuration!!, showToast).execute()
  }

}
