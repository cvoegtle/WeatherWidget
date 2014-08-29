package org.voegtle.weatherwidget;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;
import org.voegtle.weatherwidget.location.LocationFactory;
import org.voegtle.weatherwidget.location.WeatherLocation;
import org.voegtle.weatherwidget.preferences.WeatherSettingsReader;

import java.util.List;

public class WeatherWidgetProvider extends AppWidgetProvider implements SharedPreferences.OnSharedPreferenceChangeListener {
  private PendingIntent refreshService;
  private List<WeatherLocation> locations;
  private Resources res;
  private RemoteViews remoteViews;
  private AlarmManager alarmManager;
  private int intervall = -1;

  public WeatherWidgetProvider() {
  }

  @Override
  public void onUpdate(Context context, final AppWidgetManager appWidgetManager, int[] appWidgetIds) {
    ensureResources(context);

    for (int widgetId : appWidgetIds) {

      PendingIntent pendingOpenApp = createOpenAppIntent(context);
      for (WeatherLocation location : locations) {
        remoteViews.setOnClickPendingIntent(location.getWeatherViewId(), pendingOpenApp);
      }

      remoteViews.setOnClickPendingIntent(R.id.refresh_button, refreshService);

      appWidgetManager.updateAppWidget(widgetId, remoteViews);
    }

    rescheduleService();
    runServiceNow();
  }

  private void ensureResources(Context context) {
    if (res == null) {
      res = context.getResources();
      alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

      refreshService = createRefreshIntent(context);

      remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_weather);
      locations = LocationFactory.buildWeatherLocations(res);

      SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
      preferences.registerOnSharedPreferenceChangeListener(this);
      processPreferences(preferences);
    }
  }

  @Override
  public void onDisabled(Context context) {
    // aus Gründen, die ich nicht verstehe sind alarmManager und refreshService hier null
    // --> neu anlegen. canceln ist wichtig.
    refreshService = createRefreshIntent(context);
    alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    cancelAlarmService();
  }

  private void rescheduleService() {
    cancelAlarmService();

    if (intervall > 0) {
      alarmManager.setInexactRepeating(AlarmManager.RTC, System.currentTimeMillis(), intervall * 60 * 1000, refreshService);
    }
  }

  private void runServiceNow() {
    alarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + 10, refreshService);
  }

  private void cancelAlarmService() {
    if (refreshService != null) {
      alarmManager.cancel(refreshService);
    }
  }

  @Override
  public void onSharedPreferenceChanged(SharedPreferences preferences, String s) {
    boolean preferencesChanged = processPreferences(preferences);
    if (preferencesChanged) {
      rescheduleService();
    }
  }

  /**
   * @return true if preferences changed
   */
  private boolean processPreferences(SharedPreferences preferences) {
    WeatherSettingsReader weatherSettingsReader = new WeatherSettingsReader();
    weatherSettingsReader.read(preferences, locations);

    int oldIntervall = intervall;
    intervall = weatherSettingsReader.readIntervall(preferences);

    return oldIntervall != intervall;
  }

  private PendingIntent createOpenAppIntent(Context context) {
    Intent intentOpenApp = new Intent(context, WeatherActivity.class);
    return PendingIntent.getActivity(context, 0, intentOpenApp, PendingIntent.FLAG_UPDATE_CURRENT);
  }

  private PendingIntent createRefreshIntent(Context context) {
    final Intent refreshIntent = new Intent(context, WidgetRefreshService.class);
    return PendingIntent.getService(context, 0, refreshIntent, PendingIntent.FLAG_UPDATE_CURRENT);
  }


}