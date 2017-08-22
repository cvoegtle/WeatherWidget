package org.voegtle.weatherwidget

import org.voegtle.weatherwidget.widget.WidgetRefreshService

class WeatherWidgetProviderLarge : AbstractWidgetProvider() {
  override val widgetServiceClass = WidgetRefreshService::class.java
}