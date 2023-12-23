package org.voegtle.weatherwidget.widget

import android.app.Service
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Resources
import android.os.IBinder
import android.preference.PreferenceManager
import org.voegtle.weatherwidget.notification.NotificationSystemManager
import org.voegtle.weatherwidget.preferences.ApplicationSettings
import org.voegtle.weatherwidget.preferences.WeatherSettingsReader
import java.util.Date

class WidgetRefreshService : Service(), SharedPreferences.OnSharedPreferenceChangeListener {
  private val WAITING_PERIOD = (5 * 60 * 1000).toLong()

  private var res: Resources? = null

  private var configuration: ApplicationSettings? = null
  private var screenPainterFactory: ScreenPainterFactory? = null
  private var notificationManager: NotificationSystemManager? = null
  private var lastUpdate: Long = Date().time - WAITING_PERIOD

  override fun onCreate() {
    super.onCreate()
    ensureResources()

    val preferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
    preferences.registerOnSharedPreferenceChangeListener(this)

    processPreferences(preferences)
  }

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    val result: Int
    try {
      ensureResources()
      updateWidget()
    } finally {
      result = super.onStartCommand(intent, flags, startId)
    }
    return result
  }

  private fun updateWidget() {
    lastUpdate = Date().time

    val screenPainters = screenPainterFactory!!.createScreenPainters()
    WidgetUpdateTask(applicationContext, configuration!!, screenPainters).execute()
  }


  override fun onSharedPreferenceChanged(preferences: SharedPreferences, s: String?) {
    processPreferences(preferences)
  }

  override fun onDestroy() {
    val preferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
    preferences.unregisterOnSharedPreferenceChangeListener(this)
    super.onDestroy()
  }

  private fun processPreferences(preferences: SharedPreferences) {
    val weatherSettingsReader = WeatherSettingsReader(res!!)
    configuration = weatherSettingsReader.read(preferences)

    screenPainterFactory = ScreenPainterFactory(this, configuration!!)
  }

  private fun ensureResources() {
    if (res == null) {
      res = resources
      configuration = ApplicationSettings()
      notificationManager = NotificationSystemManager(this, configuration!!)
      screenPainterFactory = ScreenPainterFactory(this, configuration!!)
    }
  }

  private val isLastUpdateOutdated: Boolean
    get() = Date().time - lastUpdate > WAITING_PERIOD


  override fun onBind(intent: Intent): IBinder? {
    return null
  }
}
