package org.voegtle.weatherwidget

import org.voegtle.weatherwidget.widget.WidgetRefreshService

class WeatherWidgetProvider : AbstractWidgetProvider() {

  override val widgetServiceClass = WidgetRefreshService::class.java

}