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

  private fun intentFlags() = PendingIntent.FLAG_IMMUTABLE

}
