package org.voegtle.weatherwidget;

import android.content.Context;
import org.voegtle.weatherwidget.system.AbstractWidgetUpdateManager;
import org.voegtle.weatherwidget.system.WidgetUpdateManagerLarge;
import org.voegtle.weatherwidget.widget.WidgetRefreshServiceLarge;

public class WeatherWidgetProviderLarge extends AbstractWidgetProvider {

  public WeatherWidgetProviderLarge() {
  }

  @Override
  Class<?> getWidgetServiceClass() {
    return WidgetRefreshServiceLarge.class;
  }

  @Override
  AbstractWidgetUpdateManager getUpdateManager(Context context) {
    return new WidgetUpdateManagerLarge(context);
  }
}