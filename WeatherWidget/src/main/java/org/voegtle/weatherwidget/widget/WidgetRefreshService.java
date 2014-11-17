package org.voegtle.weatherwidget.widget;

import org.voegtle.weatherwidget.WeatherWidgetProvider;

public class WidgetRefreshService extends AbstractWidgetRefreshService {
  @Override
  protected Class<?> getWidgetProviderClass() {
    return WeatherWidgetProvider.class;
  }

  @Override
  protected boolean isDetailed() {
    return false;
  }
}
