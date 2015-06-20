package org.voegtle.weatherwidget;

import android.content.Context;
import org.voegtle.weatherwidget.system.AbstractWidgetUpdateManager;
import org.voegtle.weatherwidget.system.WidgetUpdateManager;
import org.voegtle.weatherwidget.widget.WidgetRefreshService;

public class WeatherWidgetProviderLarge extends AbstractWidgetProvider {

  public WeatherWidgetProviderLarge() {
  }

  @Override
  Class<?> getWidgetServiceClass() {
    return WidgetRefreshService.class;
  }

  @Override
  AbstractWidgetUpdateManager getUpdateManager(Context context) {
    return new WidgetUpdateManager(context);
  }
}