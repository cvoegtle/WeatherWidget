package org.voegtle.weatherwidget

import android.content.Context
import org.voegtle.weatherwidget.system.AbstractWidgetUpdateManager
import org.voegtle.weatherwidget.system.WidgetUpdateManager
import org.voegtle.weatherwidget.widget.WidgetRefreshService

class WeatherWidgetProvider : AbstractWidgetProvider() {

  override fun getWidgetServiceClass(): Class<*> {
    return WidgetRefreshService::class.java
  }

  override fun getUpdateManager(context: Context): AbstractWidgetUpdateManager {
    return WidgetUpdateManager(context)
  }
}