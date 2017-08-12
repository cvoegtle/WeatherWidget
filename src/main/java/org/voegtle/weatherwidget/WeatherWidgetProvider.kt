package org.voegtle.weatherwidget

import org.voegtle.weatherwidget.widget.WidgetRefreshService

class WeatherWidgetProvider : AbstractWidgetProvider() {

  override fun getWidgetServiceClass(): Class<*> {
    return WidgetRefreshService::class.java
  }

}