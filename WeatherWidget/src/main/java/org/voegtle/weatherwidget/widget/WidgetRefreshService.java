package org.voegtle.weatherwidget.widget;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.RemoteViews;
import org.voegtle.weatherwidget.R;
import org.voegtle.weatherwidget.WeatherWidgetProvider;
import org.voegtle.weatherwidget.location.LocationFactory;
import org.voegtle.weatherwidget.location.WeatherLocation;
import org.voegtle.weatherwidget.preferences.WeatherSettingsReader;
import org.voegtle.weatherwidget.system.WidgetUpdateManager;

import java.util.List;

public class WidgetRefreshService extends Service implements SharedPreferences.OnSharedPreferenceChangeListener {
  private Resources res;
  private RemoteViews remoteViews;
  private List<WeatherLocation> locations;
  private int intervall = -1;

  @Override
  public void onCreate() {
    super.onCreate();
    ensureResources();

    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    preferences.registerOnSharedPreferenceChangeListener(this);

    processPreferences(preferences);
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    updateWidget();
    return super.onStartCommand(intent, flags, startId);
  }

  private void updateWidget() {
    ensureResources();

    ComponentName thisWidget = new ComponentName(this, WeatherWidgetProvider.class);
    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
    int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
    new WidgetUpdateTask(getApplicationContext(), appWidgetManager, allWidgetIds, remoteViews, locations).execute();
  }

  @Override
  public void onSharedPreferenceChanged(SharedPreferences preferences, String s) {
    processPreferences(preferences);
  }

  @Override
  public void onDestroy() {
    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    preferences.unregisterOnSharedPreferenceChangeListener(this);
    super.onDestroy();
  }

  private void processPreferences(SharedPreferences preferences) {
    WeatherSettingsReader weatherSettingsReader = new WeatherSettingsReader();
    weatherSettingsReader.read(preferences, locations);

    int oldIntervall = intervall;
    intervall = weatherSettingsReader.readIntervall(preferences);
    if (oldIntervall != intervall) {
      new WidgetUpdateManager(getApplicationContext()).rescheduleService();
    }

    for (WeatherLocation location : locations) {
      boolean show = location.getPreferences().isShowInWidget();
      updateVisibility(location.getWeatherLineId(), show);
    }
    updateAllWidgets();
  }

  private void updateVisibility(int id, boolean isVisible) {
    remoteViews.setViewVisibility(id, isVisible ? View.VISIBLE : View.GONE);
  }

  protected void updateAllWidgets() {
    ComponentName thisWidget = new ComponentName(this, WeatherWidgetProvider.class);
    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
    int[] widgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

    for (int widgetId : widgetIds) {
      appWidgetManager.updateAppWidget(widgetId, remoteViews);
    }
  }

  private void ensureResources() {
    if (res == null) {
      res = getResources();
      remoteViews = new RemoteViews(getPackageName(), R.layout.widget_weather);
      locations = LocationFactory.buildWeatherLocations(res);
    }
  }

  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }
}
