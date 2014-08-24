package org.voegtle.weatherwidget.util;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import org.voegtle.weatherwidget.R;
import org.voegtle.weatherwidget.data.WeatherData;
import org.voegtle.weatherwidget.location.WeatherLocation;
import org.voegtle.weatherwidget.notification.NotificationSystemManager;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class WeatherWidgetUpdater {
  private Context context;
  final AppWidgetManager appWidgetManager;
  final int widgetId;
  final RemoteViews remoteViews;
  final List<WeatherLocation> locations;
  private final WeatherDataFetcher weatherDataFetcher;
  private DecimalFormat numberFormat;

  public WeatherWidgetUpdater(Context context, AppWidgetManager appWidgetManager, int widgetId,
                              RemoteViews remoteViews) {
    this(context, appWidgetManager, widgetId, remoteViews, new ArrayList<WeatherLocation>());
  }

  public WeatherWidgetUpdater(Context context, AppWidgetManager appWidgetManager, int widgetId,
                              RemoteViews remoteViews,
                              List<WeatherLocation> locations) {
    this.context = context;
    this.appWidgetManager = appWidgetManager;
    this.widgetId = widgetId;
    this.remoteViews = remoteViews;
    this.locations = locations;
    this.weatherDataFetcher = new WeatherDataFetcher();

    this.numberFormat = (DecimalFormat) NumberFormat.getNumberInstance(Locale.GERMANY);
    this.numberFormat.applyPattern("###.#");

  }

  public void startWeatherUpdateThread() {

    final Runnable updater = new Runnable() {
      @Override
      public void run() {
        showDataIsInvalid();

        try {
          HashMap<String, WeatherData> data = weatherDataFetcher.fetchAllWeatherDataFromServer();

          Thread.sleep(250, 0);

          for (WeatherLocation location : locations) {
            visualizeData(location.getWeatherViewId(), location.getShortName(), data.get(location.getKey().toString()));
          }

          NotificationSystemManager notificationManager = new NotificationSystemManager(context);
          notificationManager.checkDataForAlert(data);
        } catch (Throwable th) {
          Log.e(WeatherDataUpdater.class.toString(), "Failed to update View", th);
        } finally {
          remoteViews.setViewVisibility(R.id.refresh_button, View.VISIBLE);
          appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }
      }
    };
    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    scheduler.schedule(updater, 1, TimeUnit.MILLISECONDS);
  }

  private void showDataIsInvalid() {
    remoteViews.setViewVisibility(R.id.refresh_button, View.INVISIBLE);

    for (WeatherLocation location : locations) {
      remoteViews.setTextViewText(location.getWeatherViewId(), location.getShortName() + " " + "-");
    }
    appWidgetManager.updateAppWidget(widgetId, remoteViews);
  }

  private void visualizeData(int widgetId, String locationName, WeatherData data) {
    remoteViews.setTextColor(widgetId, ColorUtil.byAge(data.getTimestamp()));
    remoteViews.setTextViewText(widgetId, locationName + " "
        + retrieveFormattedTemperature(data));
  }

  public void startSmallWeatherScheduler(final String weatherServerUrl) {
    final Runnable updater = new Runnable() {
      @Override
      public void run() {
        try {
          remoteViews.setTextViewText(R.id.weather_small, "-");
          WeatherData data = weatherDataFetcher.fetchWeatherDataFromUrl(weatherServerUrl);

          remoteViews.setTextViewText(R.id.weather_small, retrieveFormattedTemperature(data));
          remoteViews.setTextColor(R.id.weather_small, ColorUtil.byAge(data.getTimestamp()));
          new UserFeedbackWidget(context).showMessage(R.string.message_data_updated);
        } catch (Throwable th) {
          Log.e(WeatherDataUpdater.class.toString(), "Failed to update View", th);
        } finally {
          appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }
      }
    };
    ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    scheduler.execute(updater);

  }

  private String retrieveFormattedTemperature(WeatherData data) {
    String formattedTemperature;
    Float temperature = data.getTemperature();
    if (temperature != null) {
      formattedTemperature = numberFormat.format(temperature) + "°C";
    } else {
      formattedTemperature = "-";
    }
    return formattedTemperature;
  }

}
