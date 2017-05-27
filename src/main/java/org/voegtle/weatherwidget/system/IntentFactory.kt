package org.voegtle.weatherwidget.system

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import org.voegtle.weatherwidget.WeatherActivity
import org.voegtle.weatherwidget.widget.WidgetRefreshService

object IntentFactory {
  fun createRefreshIntent(context: Context, cls: Class<*>): PendingIntent {
    val refreshIntent = Intent(context, cls)
    return PendingIntent.getService(context, 0, refreshIntent, PendingIntent.FLAG_UPDATE_CURRENT)
  }

  fun createOpenAppIntent(context: Context): PendingIntent {
    val intentOpenApp = Intent(context, WeatherActivity::class.java)
    return PendingIntent.getActivity(context, 0, intentOpenApp, PendingIntent.FLAG_UPDATE_CURRENT)
  }

}
