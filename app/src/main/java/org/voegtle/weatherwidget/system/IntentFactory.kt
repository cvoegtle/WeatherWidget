package org.voegtle.weatherwidget.system

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import org.voegtle.weatherwidget.WeatherActivity

object IntentFactory {
  fun createRefreshIntent(context: Context, cls: Class<*>): PendingIntent {
    val refreshIntent = Intent(context, cls)
    return PendingIntent.getService(context, 0, refreshIntent, intentFlags())
  }

  fun createOpenAppIntent(context: Context): PendingIntent {
    val intentOpenApp = Intent(context, WeatherActivity::class.java)
    return PendingIntent.getActivity(context, 0, intentOpenApp, intentFlags())
  }

  fun createNotificationIntent(context: Context): PendingIntent? {
    val intentOpenApp = Intent(context, WeatherActivity::class.java)
    intentOpenApp.action = WeatherActivity.ANDROID8
    return PendingIntent.getActivity(context, 0, intentOpenApp, intentFlags())
  }


  private fun intentFlags() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M )  PendingIntent.FLAG_IMMUTABLE else PendingIntent.FLAG_UPDATE_CURRENT

}
