package org.voegtle.weatherwidget.widget;

import org.voegtle.weatherwidget.WeatherWidgetProvider;

public class WidgetRefreshService extends AbstractWidgetRefreshService {
  @Override
  Class<?> getWidgetProviderClass() {
    return WeatherWidgetProvider.class;
  }
}
