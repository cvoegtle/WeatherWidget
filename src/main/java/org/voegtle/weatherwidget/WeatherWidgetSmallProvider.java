package org.voegtle.weatherwidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;
import org.voegtle.weatherwidget.preferences.ApplicationSettings;
import org.voegtle.weatherwidget.preferences.WeatherSettingsReader;
import org.voegtle.weatherwidget.widget.SmallWidgetScreenPainter;
import org.voegtle.weatherwidget.widget.SmallWidgetUpdateTask;

class WeatherWidgetSmallProvider extends AppWidgetProvider {
  private final int resourceKeyCity;
  private final String weatherDataUrl;


  WeatherWidgetSmallProvider(final String weatherDataUrl, final int resourceKeyCity) {
    this.resourceKeyCity = resourceKeyCity;
    this.weatherDataUrl = weatherDataUrl;
  }

  @Override
  public void onUpdate(Context context, final AppWidgetManager appWidgetManager, int[] appWidgetIds) {
    final Resources res = context.getResources();
    final RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_small_weather);
    remoteViews.setTextViewText(R.id.weather_location, res.getString(resourceKeyCity));

    SmallWidgetScreenPainter screenPainter = new SmallWidgetScreenPainter(appWidgetManager, appWidgetIds, remoteViews);

    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
    WeatherSettingsReader weatherSettingsReader = new WeatherSettingsReader(res);
    ApplicationSettings configuration = weatherSettingsReader.read(preferences);


    new SmallWidgetUpdateTask(context, configuration, screenPainter).execute(weatherDataUrl);

    for (int widgetId : appWidgetIds) {
      Intent intentRefresh = new Intent(context, this.getClass());
      intentRefresh.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
      intentRefresh.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
      PendingIntent pendingRefresh = PendingIntent.getBroadcast(context, 0, intentRefresh, PendingIntent.FLAG_UPDATE_CURRENT);

      remoteViews.setOnClickPendingIntent(R.id.weather_small, pendingRefresh);

      appWidgetManager.updateAppWidget(widgetId, remoteViews);

    }

    super.onUpdate(context, appWidgetManager, appWidgetIds);
  }


}