package org.voegtle.weatherwidget.system


import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import org.voegtle.weatherwidget.preferences.ApplicationSettings
import org.voegtle.weatherwidget.preferences.WeatherSettingsReader

abstract class AbstractWidgetUpdateManager protected constructor(context: Context, cls: Class<*>) {

  private val alarmManager: AlarmManager
  private val refreshService: PendingIntent
  private var interval: Int? = null

  init {
    alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    refreshService = IntentFactory.createRefreshIntent(context, cls)

    val preferences = PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
    processPreferences(preferences, context)
  }

  fun runServiceNow() {
    alarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + 10, refreshService)
  }

  fun rescheduleService() {
    cancelAlarmService()

    if (interval != null && interval!! > 0) {
      alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 10, (interval!! * 60 * 1000).toLong(), refreshService)
    }
  }

  fun cancelAlarmService() {
    alarmManager.cancel(refreshService)
  }


  private fun processPreferences(preferences: SharedPreferences, context: Context) {
    val weatherSettingsReader = WeatherSettingsReader(context)
    val (_, _, updateInterval) = weatherSettingsReader.read(preferences)
    interval = updateInterval
  }


}
