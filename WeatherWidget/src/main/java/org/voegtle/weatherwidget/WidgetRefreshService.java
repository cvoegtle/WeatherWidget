package org.voegtle.weatherwidget;

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
import org.voegtle.weatherwidget.location.LocationFactory;
import org.voegtle.weatherwidget.location.WeatherLocation;
import org.voegtle.weatherwidget.preferences.WeatherSettingsReader;
import org.voegtle.weatherwidget.util.WidgetUpdateTask;

import java.util.List;

public class WidgetRefreshService extends Service implements SharedPreferences.OnSharedPreferenceChangeListener {
  private Resources res;
  private RemoteViews remoteViews;
  private List<WeatherLocation> locations;

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

  private void ensureResources() {
    if (res == null) {
      res = getResources();
      remoteViews = new RemoteViews(getPackageName(), R.layout.widget_weather);
      locations = LocationFactory.buildWeatherLocations(res);
    }
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
    updateWidget();
  }

  private void processPreferences(SharedPreferences preferences) {
    WeatherSettingsReader weatherSettingsReader = new WeatherSettingsReader();
    weatherSettingsReader.read(preferences, locations);

    for (WeatherLocation location : locations) {
      boolean show = location.getPreferences().isShowInWidget();
      updateVisibility(location.getWeatherViewId(), show);
    }
  }

  private void updateVisibility(int id, boolean isVisible) {
    remoteViews.setViewVisibility(id, isVisible ? View.VISIBLE : View.GONE);
  }

  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }
}
