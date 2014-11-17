package org.voegtle.weatherwidget.widget;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.RemoteViews;
import org.voegtle.weatherwidget.R;
import org.voegtle.weatherwidget.location.LocationFactory;
import org.voegtle.weatherwidget.location.WeatherLocation;
import org.voegtle.weatherwidget.preferences.WeatherSettingsReader;
import org.voegtle.weatherwidget.system.IntentFactory;
import org.voegtle.weatherwidget.system.WidgetUpdateManager;
import org.voegtle.weatherwidget.util.NotificationTask;

import java.util.List;

public abstract class AbstractWidgetRefreshService extends Service implements SharedPreferences.OnSharedPreferenceChangeListener {
  private Resources res;
  private RemoteViews remoteViews;
  private List<WeatherLocation> locations;
  private Integer interval;

  abstract Class<?> getWidgetProviderClass();

  @Override
  public void onCreate() {
    super.onCreate();
    ensureResources();

    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    preferences.registerOnSharedPreferenceChangeListener(this);

    processPreferences(preferences);
    setWidgetIntents();
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    setWidgetIntents();
    updateWidget();
    return super.onStartCommand(intent, flags, startId);
  }

  private void updateWidget() {
    ensureResources();

    ComponentName thisWidget = new ComponentName(this, getWidgetProviderClass());
    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
    int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
    WidgetScreenPainter screenPainter = new WidgetScreenPainter(appWidgetManager, allWidgetIds, remoteViews, locations);
    new WidgetUpdateTask(getApplicationContext(), screenPainter).execute();
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

  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    setWidgetIntents();
  }

  private void processPreferences(SharedPreferences preferences) {
    WeatherSettingsReader weatherSettingsReader = new WeatherSettingsReader();
    weatherSettingsReader.read(preferences, locations);

    Integer oldInterval = interval;
    interval = weatherSettingsReader.readIntervall(preferences);

    if (oldInterval != interval) {
      new WidgetUpdateManager(getApplicationContext()).rescheduleService();
    }

    if (oldInterval != null && oldInterval != interval) {
      String message;
      if (interval > 0) {
        message = getApplicationContext().getString(R.string.intervall_changed) + " " + interval + "min";
      } else {
        message = getApplicationContext().getString(R.string.update_deaktiviert);
      }
      new NotificationTask(getApplicationContext(), message).execute();
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
    ComponentName thisWidget = new ComponentName(this, getWidgetProviderClass());
    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
    int[] widgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

    for (int widgetId : widgetIds) {
      appWidgetManager.updateAppWidget(widgetId, remoteViews);
    }
  }

  private void setWidgetIntents() {
    PendingIntent pendingOpenApp = IntentFactory.createOpenAppIntent(getApplicationContext());
    for (WeatherLocation location : locations) {
      remoteViews.setOnClickPendingIntent(location.getWeatherViewId(), pendingOpenApp);
    }

    remoteViews.setOnClickPendingIntent(R.id.refresh_button, IntentFactory.createRefreshIntent(getApplicationContext(), this.getClass()));
    updateAllWidgets();
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
