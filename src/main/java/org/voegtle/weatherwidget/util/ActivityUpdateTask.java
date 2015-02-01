package org.voegtle.weatherwidget.util;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.RemoteViews;
import org.voegtle.weatherwidget.*;
import org.voegtle.weatherwidget.data.WeatherData;
import org.voegtle.weatherwidget.location.LocationIdentifier;
import org.voegtle.weatherwidget.location.LocationView;
import org.voegtle.weatherwidget.location.WeatherLocation;
import org.voegtle.weatherwidget.notification.NotificationSystemManager;
import org.voegtle.weatherwidget.preferences.ApplicationSettings;
import org.voegtle.weatherwidget.widget.WidgetScreenPainter;

import java.text.SimpleDateFormat;
import java.util.HashMap;

public class ActivityUpdateTask extends AsyncTask<Void, Void, HashMap<LocationIdentifier, WeatherData>> {
  private final WidgetScreenPainter screenPainter;
  private final WidgetScreenPainter screenPainterLarge;
  private WeatherActivity activity;
  private DataFormatter formatter;
  private final WeatherDataFetcher weatherDataFetcher;

  private boolean showToast;
  private ApplicationSettings configuration;

  public ActivityUpdateTask(WeatherActivity activity, ApplicationSettings configuration, boolean showToast) {
    this.configuration = configuration;
    this.activity = activity;
    this.screenPainter = createScreenPainter(false, WeatherWidgetProvider.class);
    this.screenPainterLarge = createScreenPainter(true, WeatherWidgetProviderLarge.class);

    this.showToast = showToast;
    this.formatter = new DataFormatter(activity.getResources());
    this.weatherDataFetcher = new WeatherDataFetcher();
  }

  private WidgetScreenPainter createScreenPainter(boolean large, Class<? extends AbstractWidgetProvider> providerClass) {
    Context applicationContext = activity.getApplicationContext();
    ComponentName thisWidget = new ComponentName(applicationContext, providerClass);
    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(applicationContext);
    RemoteViews remoteViews = new RemoteViews(applicationContext.getPackageName(), R.layout.widget_weather);

    int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

    return new WidgetScreenPainter(appWidgetManager, allWidgetIds, remoteViews, configuration.getLocations(), large);
  }

  @Override
  protected HashMap<LocationIdentifier, WeatherData> doInBackground(Void... voids) {
    return weatherDataFetcher.fetchAllWeatherDataFromServer(configuration.getLocations(), configuration.getSecret());
  }

  @Override
  protected void onPostExecute(HashMap<LocationIdentifier, WeatherData> data) {
    try {
      updateViewData(data);
      updateWidgets(data);

      new UserFeedback(activity).showMessage(R.string.message_data_updated, showToast);

      NotificationSystemManager notificationManager = new NotificationSystemManager(activity, configuration);
      notificationManager.checkDataForAlert(data);
    } catch (Throwable th) {
      new UserFeedback(activity).showMessage(R.string.message_data_update_failed, showToast);
      Log.e(ActivityUpdateTask.class.toString(), "Failed to update View", th);
    }
  }

  private void updateViewData(HashMap<LocationIdentifier, WeatherData> data) {
    for (WeatherLocation location : configuration.getLocations()) {
      WeatherData locationData = data.get(location.getKey());
      if (locationData != null) {
        updateWeatherLocation(location.getWeatherViewId(),
            location.getName(), locationData);
      }
    }
  }

  private void updateWidgets(HashMap<LocationIdentifier, WeatherData> data) {
    screenPainter.updateWidgetData(data);
    screenPainter.showDataIsValid();
    screenPainterLarge.updateWidgetData(data);
    screenPainterLarge.showDataIsValid();
  }

  private void updateWeatherLocation(int locationId, String locationName, WeatherData data) {
    final LocationView contentView = (LocationView) activity.findViewById(locationId);

    final int color = ColorUtil.byAge(configuration.getColorScheme(), data.getTimestamp());
    final String caption = getCaption(locationName, data);
    final String text = formatter.formatForActivity(data);

    updateView(contentView, caption, text, color);
  }

  private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

  private String getCaption(String locationName, WeatherData data) {
    return locationName + " - " + sdf.format(data.getTimestamp());
  }

  private void updateView(final LocationView view, final String caption, final String text, final int color) {
    view.setCaption(caption);
    view.setData(text);
    view.setTextColor(color);
  }
}