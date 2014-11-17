package org.voegtle.weatherwidget;

import android.content.Context;
import org.voegtle.weatherwidget.system.AbstractWidgetUpdateManager;
import org.voegtle.weatherwidget.system.WidgetUpdateManager;
import org.voegtle.weatherwidget.widget.WidgetRefreshService;

public class WeatherWidgetProvider extends AbstractWidgetProvider {
  public WeatherWidgetProvider() {
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