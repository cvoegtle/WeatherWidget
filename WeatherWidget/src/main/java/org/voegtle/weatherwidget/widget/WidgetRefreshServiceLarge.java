package org.voegtle.weatherwidget.widget;

import org.voegtle.weatherwidget.WeatherWidgetProviderLarge;

public class WidgetRefreshServiceLarge extends AbstractWidgetRefreshService {

  @Override
  Class<?> getWidgetProviderClass() {
    return WeatherWidgetProviderLarge.class;
  }
}
