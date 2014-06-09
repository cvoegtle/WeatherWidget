package org.voegtle.weatherwidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.widget.RemoteViews;
import org.voegtle.weatherwidget.util.WeatherWidgetUpdater;

public class WeatherWidgetProvider extends AppWidgetProvider {

  public WeatherWidgetProvider() {

  }


  @Override
  public void onUpdate(Context context, final AppWidgetManager appWidgetManager, int[] appWidgetIds) {
    final Resources res = context.getResources( );
    final RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_weather);

    ComponentName thisWidget = new ComponentName(context, WeatherWidgetProvider.class);
    int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
    for (int widgetId : allWidgetIds) {

      new WeatherWidgetUpdater(appWidgetManager, widgetId, remoteViews, res)
              .startWeatherScheduler();

      Intent intentOpenApp = new Intent(context, WeatherActivity.class);
      PendingIntent pendingOpenApp = PendingIntent.getActivity(context, 0, intentOpenApp, 0);

      remoteViews.setOnClickPendingIntent(R.id.weather_freiburg, pendingOpenApp);
      remoteViews.setOnClickPendingIntent(R.id.weather_paderborn, pendingOpenApp);
      remoteViews.setOnClickPendingIntent(R.id.weather_bonn, pendingOpenApp);

      Intent intentRefresh = new Intent(context, WeatherWidgetProvider.class);
      intentRefresh.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
      intentRefresh.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
      PendingIntent pendingRefresh = PendingIntent.getBroadcast(context, 0, intentRefresh, PendingIntent.FLAG_UPDATE_CURRENT);

      remoteViews.setOnClickPendingIntent(R.id.refresh_button, pendingRefresh);

      appWidgetManager.updateAppWidget(widgetId, remoteViews);

    }

    super.onUpdate(context, appWidgetManager, appWidgetIds);
  }

}
