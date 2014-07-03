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
import org.voegtle.weatherwidget.preferences.WeatherSettings;
import org.voegtle.weatherwidget.util.WeatherWidgetUpdater;

public class WeatherWidgetProvider extends AppWidgetProvider implements SharedPreferences.OnSharedPreferenceChangeListener {
  Resources res;
  RemoteViews remoteViews;

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

  @Override
  public void onSharedPreferenceChanged(SharedPreferences preferences, String s) {
    setupUserInterface(preferences);
  }

  private void setupUserInterface(SharedPreferences preferences) {
    WeatherSettings weatherSettings = new WeatherSettings(preferences);

    boolean showPaderborn = weatherSettings.getPaderborn().isShowInWidget();
    updateVisibility(R.id.weather_paderborn, showPaderborn);

    boolean showFreiburg = weatherSettings.getFreiburg().isShowInWidget();
    updateVisibility(R.id.weather_freiburg, showFreiburg);

    boolean showBonn = weatherSettings.getBonn().isShowInWidget();
    updateVisibility(R.id.weather_bonn, showBonn);
  }

  private void updateVisibility(int id, boolean isVisible) {
    remoteViews.setViewVisibility(id, isVisible ? View.VISIBLE : View.GONE);
  }


}