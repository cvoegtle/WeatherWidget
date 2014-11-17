package org.voegtle.weatherwidget.widget;

import org.voegtle.weatherwidget.WeatherWidgetProviderLarge;

public class WidgetRefreshServiceLarge extends AbstractWidgetRefreshService {

  @Override
  protected Class<?> getWidgetProviderClass() {
    return WeatherWidgetProviderLarge.class;
  }

  @Override
  protected boolean isDetailed() {
    return true;
  }
}
