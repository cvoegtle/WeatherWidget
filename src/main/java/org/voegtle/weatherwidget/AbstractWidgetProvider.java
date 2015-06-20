package org.voegtle.weatherwidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;
import org.voegtle.weatherwidget.location.WeatherLocation;
import org.voegtle.weatherwidget.preferences.ApplicationSettings;
import org.voegtle.weatherwidget.preferences.WeatherSettingsReader;
import org.voegtle.weatherwidget.system.AbstractWidgetUpdateManager;
import org.voegtle.weatherwidget.system.IntentFactory;

public abstract class AbstractWidgetProvider extends AppWidgetProvider {

  private ApplicationSettings configuration;
  private Resources res;
  private RemoteViews remoteViews;
  private AbstractWidgetUpdateManager updateManager;

  abstract Class<?> getWidgetServiceClass();

  abstract AbstractWidgetUpdateManager getUpdateManager(Context context);

  @Override
  public void onEnabled(Context context) {
    ensureResources(context);

    updateManager.rescheduleService();
    super.onEnabled(context);
  }

  @Override
  public void onUpdate(Context context, final AppWidgetManager appWidgetManager, int[] appWidgetIds) {
    ensureResources(context);


    for (int widgetId : appWidgetIds) {
      PendingIntent pendingOpenApp = IntentFactory.createOpenAppIntent(context.getApplicationContext());
      for (WeatherLocation location : configuration.getLocations()) {
        remoteViews.setOnClickPendingIntent(location.getWeatherViewId(), pendingOpenApp);
      }

      remoteViews.setOnClickPendingIntent(R.id.refresh_button,
          IntentFactory.createRefreshIntent(context.getApplicationContext(), getWidgetServiceClass()));

      appWidgetManager.updateAppWidget(widgetId, remoteViews);
    }
  }

  @Override
  public void onDisabled(Context context) {
    ensureResources(context);
    updateManager.cancelAlarmService();
  }

  private void ensureResources(Context context) {
    if (res == null) {
      Context appContext = context.getApplicationContext();
      this.res = appContext.getResources();

      this.remoteViews = new RemoteViews(appContext.getPackageName(), R.layout.widget_weather);

      this.updateManager = getUpdateManager(appContext);

      SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(appContext);
      processPreferences(preferences, appContext);
    }
  }

  private void processPreferences(SharedPreferences preferences, Context context) {
    WeatherSettingsReader weatherSettingsReader = new WeatherSettingsReader(context);
    configuration = weatherSettingsReader.read(preferences);
  }

}
