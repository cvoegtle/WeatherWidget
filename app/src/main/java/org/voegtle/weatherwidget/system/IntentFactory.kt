package org.voegtle.weatherwidget.system

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import org.voegtle.weatherwidget.WeatherActivity

object IntentFactory {
  fun createRefreshIntent(context: Context): PendingIntent {
    val refreshIntent = Intent(context, WeatherActivity::class.java)
    return PendingIntent.getActivity(context, 0, refreshIntent, intentFlags())
  }

  private fun intentFlags() = PendingIntent.FLAG_IMMUTABLE

}
