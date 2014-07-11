package org.voegtle.weatherwidget.util;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import org.voegtle.weatherwidget.R;
import org.voegtle.weatherwidget.data.WeatherData;
import org.voegtle.weatherwidget.notification.NotificationSystemManager;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class WeatherWidgetUpdater {
  private Context context;
  final AppWidgetManager appWidgetManager;
  final int widgetId;
  final RemoteViews remoteViews;
  final Resources res;
  private final WeatherDataFetcher weatherDataFetcher;
  private DecimalFormat numberFormat;

  public WeatherWidgetUpdater(Context context, AppWidgetManager appWidgetManager, int widgetId, RemoteViews remoteViews, Resources res) {
    this.context = context;
    this.appWidgetManager = appWidgetManager;
    this.widgetId = widgetId;
    this.remoteViews = remoteViews;
    this.res = res;
    this.weatherDataFetcher = new WeatherDataFetcher();

    this.numberFormat = (DecimalFormat) NumberFormat.getNumberInstance(Locale.GERMANY);
    this.numberFormat.applyPattern("###.#");

  }

  public void startWeatherScheduler() {

    final Runnable updater = new Runnable() {
      @Override
      public void run() {
        remoteViews.setViewVisibility(R.id.refresh_button, View.INVISIBLE);
        remoteViews.setTextViewText(R.id.weather_paderborn, res.getString(R.string.city_paderborn) + " " + "-");
        remoteViews.setTextViewText(R.id.weather_freiburg, res.getString(R.string.city_freiburg) + " " + "-");
        remoteViews.setTextViewText(R.id.weather_bonn, res.getString(R.string.city_bonn) + " -");

        try {
          HashMap<String, WeatherData> data = weatherDataFetcher.fetchAllWeatherDataFromServer();

          Thread.sleep(250, 0);

          visualizeData(R.id.weather_paderborn, R.string.city_paderborn, data.get("Paderborn"));
          visualizeData(R.id.weather_freiburg, R.string.city_freiburg, data.get("Freiburg"));
          visualizeData(R.id.weather_bonn, R.string.city_bonn, data.get("Bonn"));

          NotificationSystemManager notificationManager = new NotificationSystemManager(context);
          notificationManager.checkDataForAlert(data);
        } catch (Throwable th) {
          Log.e(WeatherDataUpdater.class.toString(), "Failed to update View");
        } finally {
          remoteViews.setViewVisibility(R.id.refresh_button, View.VISIBLE);
          appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }
      }
    };
    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    scheduler.schedule(updater, 1, TimeUnit.MILLISECONDS);
  }

  private void visualizeData(int widgetId, int locationId, WeatherData data) {
    remoteViews.setTextColor(widgetId, ColorUtil.byAge(data.getTimestamp()));
    remoteViews.setTextViewText(widgetId, res.getString(locationId) + " "
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
          new UserFeedbackWidget(context, res).showMessage(R.string.message_data_updated);
        } catch (Throwable th) {
          Log.e(WeatherDataUpdater.class.toString(), "Failed to update View");
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
      formattedTemperature = numberFormat.format(temperature) + res.getString(R.string.degree_centigrade);
    } else {
      formattedTemperature = "-";
    }
    return formattedTemperature;
  }

}
