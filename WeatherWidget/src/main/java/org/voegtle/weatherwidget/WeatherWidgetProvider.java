package org.voegtle.weatherwidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.RemoteViews;
import org.voegtle.weatherwidget.location.LocationFactory;
import org.voegtle.weatherwidget.location.WeatherLocation;
import org.voegtle.weatherwidget.preferences.WeatherSettingsReader;
import org.voegtle.weatherwidget.util.WeatherWidgetUpdater;

import java.util.List;

public class WeatherWidgetProvider extends AppWidgetProvider implements SharedPreferences.OnSharedPreferenceChangeListener {
  private Resources res;
  private RemoteViews remoteViews;
  private List<WeatherLocation> locations;


  public WeatherWidgetProvider() {

  }

  @Override
  public void onEnabled(Context context) {
    ensureResources(context);
    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
    setupUserInterface(preferences);
  }

  private void ensureResources(Context context) {
    if (res == null) {
      res = context.getResources();
      remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_weather);
      SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
      preferences.registerOnSharedPreferenceChangeListener(this);

      locations = LocationFactory.buildWeatherLocations(res);
    }
  }

  @Override
  public void onUpdate(Context context, final AppWidgetManager appWidgetManager, int[] appWidgetIds) {
    ensureResources(context);
    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
    setupUserInterface(preferences);

    ComponentName thisWidget = new ComponentName(context, WeatherWidgetProvider.class);
    int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
    for (int widgetId : allWidgetIds) {

      new WeatherWidgetUpdater(context, appWidgetManager, widgetId, remoteViews, locations)
          .startWeatherScheduler();

      PendingIntent pendingOpenApp = createOpenAppIntent(context);
      for (WeatherLocation location : locations) {
        remoteViews.setOnClickPendingIntent(location.getWeatherViewId(), pendingOpenApp);
      }

      PendingIntent pendingRefresh = createRefreshIntent(context, appWidgetIds);
      remoteViews.setOnClickPendingIntent(R.id.refresh_button, pendingRefresh);

      appWidgetManager.updateAppWidget(widgetId, remoteViews);
    }

    super.onUpdate(context, appWidgetManager, appWidgetIds);
  }

  private PendingIntent createOpenAppIntent(Context context) {
    Intent intentOpenApp = new Intent(context, WeatherActivity.class);
    return PendingIntent.getActivity(context, 0, intentOpenApp, PendingIntent.FLAG_UPDATE_CURRENT);
  }

  private PendingIntent createRefreshIntent(Context context, int[] appWidgetIds) {
    Intent intentRefresh = new Intent(context, WeatherWidgetProvider.class);
    intentRefresh.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
    intentRefresh.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
    return PendingIntent.getBroadcast(context, 0, intentRefresh, PendingIntent.FLAG_UPDATE_CURRENT);
  }

  @Override
  public void onSharedPreferenceChanged(SharedPreferences preferences, String s) {
    setupUserInterface(preferences);
  }

  private void setupUserInterface(SharedPreferences preferences) {
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


}