package org.voegtle.weatherwidget.widget

import android.app.Service
import android.content.*
import android.content.res.Configuration
import android.content.res.Resources
import android.hardware.display.DisplayManager
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.preference.PreferenceManager
import android.view.Display
import org.voegtle.weatherwidget.R
import org.voegtle.weatherwidget.preferences.ApplicationSettings
import org.voegtle.weatherwidget.preferences.WeatherSettingsReader
import org.voegtle.weatherwidget.system.WidgetUpdateManager
import org.voegtle.weatherwidget.util.NotificationTask

import java.util.Date

class WidgetRefreshService : Service(), SharedPreferences.OnSharedPreferenceChangeListener {
  private val WAITING_PERIOD = (5 * 60 * 1000).toLong()

  private var res: Resources? = null

  private var configuration: ApplicationSettings? = null
  private var screenPainterFactory: ScreenPainterFactory? = null
  private var lastUpdate: Long? = null

  override fun onCreate() {
    super.onCreate()
    ensureResources()

    val preferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
    preferences.registerOnSharedPreferenceChangeListener(this)

    processPreferences(preferences)

    val filter = IntentFilter(Intent.ACTION_SCREEN_ON)
    registerReceiver(object : BroadcastReceiver() {
      override fun onReceive(context: Context, intent: Intent) {
        if (isLastUpdateOutdated) {
          ensureResources()
          updateWidget()
        }
      }
    }, filter)
  }

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    val result: Int
    try {
      if (isScreenOn) {
        ensureResources()
        updateWidget()
      }
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

  val isScreenOn: Boolean
    @SuppressWarnings()
    get() {
      val context = applicationContext
      if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
        val dm = context.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
        var screenOn = false
        dm.displays.forEach { display ->
          if (display.state != Display.STATE_OFF) {
            screenOn = true
          }
        }
        return screenOn
      } else {
        val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        // notwendig solange wir noch API Level 16 unterstützen
        return pm.isScreenOn
      }
    }


  override fun onSharedPreferenceChanged(preferences: SharedPreferences, s: String) {
    processPreferences(preferences)
  }

  override fun onDestroy() {
    val preferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
    preferences.unregisterOnSharedPreferenceChangeListener(this)
    super.onDestroy()
  }

  override fun onConfigurationChanged(newConfig: Configuration) {
    super.onConfigurationChanged(newConfig)
  }

  private fun processPreferences(preferences: SharedPreferences) {
    val oldInterval = configuration!!.updateInterval

    val weatherSettingsReader = WeatherSettingsReader(res!!)
    configuration = weatherSettingsReader.read(preferences)

    val interval = configuration!!.updateInterval

    if (oldInterval.compareTo(interval) != 0) {
      WidgetUpdateManager(applicationContext).rescheduleService()
    }

    if (oldInterval.compareTo(interval) != 0) {
      val message = getNotificationMessage(interval)
      NotificationTask(applicationContext, message).execute()
    }

    screenPainterFactory = ScreenPainterFactory(this, configuration!!)
  }

  private fun getNotificationMessage(interval: Int): String {
    val message: String
    if (interval > 0) {
      message = applicationContext.getString(R.string.intervall_changed) + " " + interval + "min"
    } else {
      message = applicationContext.getString(R.string.update_deaktiviert)
    }
    return message
  }


  private fun ensureResources() {
    if (res == null) {
      res = resources
      configuration = ApplicationSettings()
      screenPainterFactory = ScreenPainterFactory(this, configuration!!)
    }
  }

  private val isLastUpdateOutdated: Boolean
    get() = lastUpdate == null || Date().time - lastUpdate!! > WAITING_PERIOD


  override fun onBind(intent: Intent): IBinder? {
    return null
  }


}
