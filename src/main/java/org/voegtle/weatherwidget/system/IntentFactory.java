package org.voegtle.weatherwidget.system;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import org.voegtle.weatherwidget.WeatherActivity;
import org.voegtle.weatherwidget.widget.WidgetRefreshService;

public class IntentFactory {
  public static PendingIntent createRefreshIntent(Context context, Class<?> cls) {
    final Intent refreshIntent = new Intent(context, cls);
    return PendingIntent.getService(context, 0, refreshIntent, PendingIntent.FLAG_UPDATE_CURRENT);
  }

  public static PendingIntent createOpenAppIntent(Context context) {
    Intent intentOpenApp = new Intent(context, WeatherActivity.class);
    return PendingIntent.getActivity(context, 0, intentOpenApp, PendingIntent.FLAG_UPDATE_CURRENT);
  }

}
