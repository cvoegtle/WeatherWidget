package org.voegtle.weatherwidget.util;

import android.appwidget.AppWidgetManager;
import android.content.res.Resources;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import org.voegtle.weatherwidget.R;
import org.voegtle.weatherwidget.data.WeatherData;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class WeatherWidgetUpdater {
  final AppWidgetManager appWidgetManager;
  final int widgetId;
  final RemoteViews remoteViews;
  final Resources res;
  private final WeatherDataFetcher weatherDataFetcher;
  private DecimalFormat numberFormat;

  public WeatherWidgetUpdater(AppWidgetManager appWidgetManager, int widgetId, RemoteViews remoteViews, Resources res) {
    this.appWidgetManager = appWidgetManager;
    this.widgetId = widgetId;
    this.remoteViews = remoteViews;
    this.res = res;
    this.weatherDataFetcher = new WeatherDataFetcher();

    numberFormat = (DecimalFormat) NumberFormat.getNumberInstance(Locale.GERMANY);
    numberFormat.applyPattern("###.#");

  }

  public void startWeatherScheduler() {

    final Runnable updater = new Runnable() {
      @Override
      public void run() {
        remoteViews.setViewVisibility(R.id.refresh_button, View.INVISIBLE);
        try {
          remoteViews.setTextViewText(R.id.weather_paderborn, res.getString(R.string.city_paderborn) + " " + "-");
          remoteViews.setTextViewText(R.id.weather_freiburg, res.getString(R.string.city_freiburg) + " " + "-");
          remoteViews.setTextViewText(R.id.weather_bonn, res.getString(R.string.city_bonn) + "  -");

          HashMap<String, WeatherData> data = weatherDataFetcher.fetchAllWeatherDataFromServer();


          remoteViews.setTextViewText(R.id.weather_paderborn, res.getString(R.string.city_paderborn) + " "
                  + retrieveFormattedTemperature(data.get("Paderborn")));
          remoteViews.setTextViewText(R.id.weather_freiburg, res.getString(R.string.city_freiburg) + " "
                  + retrieveFormattedTemperature(data.get("Freiburg")));
          remoteViews.setTextViewText(R.id.weather_bonn, res.getString(R.string.city_bonn) + " "
              + retrieveFormattedTemperature(data.get("Bonn")));
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

  public void startSmallWeatherScheduler(final String weatherServerUrl) {
    final Runnable updater = new Runnable() {
      @Override
      public void run() {
        try {
          remoteViews.setTextViewText(R.id.weather_small, "-");
          WeatherData data = weatherDataFetcher.fetchWeatherDataFromUrl(weatherServerUrl);

          remoteViews.setTextViewText(R.id.weather_small, retrieveFormattedTemperature(data));
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
