package org.voegtle.weatherwidget.util;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.RemoteViews;
import org.voegtle.weatherwidget.data.WeatherData;
import org.voegtle.weatherwidget.location.LocationIdentifier;
import org.voegtle.weatherwidget.notification.NotificationSystemManager;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;

abstract class AbstractWidgetUpdateTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {

  private final Context context;
  private final AppWidgetManager appWidgetManager;
  private final int[] widgetIds;
  private final RemoteViews remoteViews;
  private final WeatherDataFetcher weatherDataFetcher;
  private final DecimalFormat numberFormat;

  public AbstractWidgetUpdateTask(Context context, AppWidgetManager appWidgetManager, int widgetIds[],
                                  RemoteViews remoteViews) {
    super();

    this.context = context;
    this.appWidgetManager = appWidgetManager;
    this.widgetIds = widgetIds;
    this.remoteViews = remoteViews;
    this.weatherDataFetcher = new WeatherDataFetcher();

    this.numberFormat = (DecimalFormat) NumberFormat.getNumberInstance(Locale.GERMANY);
    this.numberFormat.applyPattern("###.#");

  }

  @Override
  protected void onPreExecute() {
    showDataIsInvalid();
  }

  protected abstract void showDataIsInvalid();


  protected HashMap<LocationIdentifier, WeatherData> fetchAllWeatherData() {
    return weatherDataFetcher.fetchAllWeatherDataFromServer();
  }

  protected WeatherData fetchWeatherData(String weatherServerUrl) {
    return weatherDataFetcher.fetchWeatherDataFromUrl(weatherServerUrl);
  }

  protected void checkDataForAlert(HashMap<LocationIdentifier, WeatherData> data) {
    NotificationSystemManager notificationManager = new NotificationSystemManager(context);
    notificationManager.checkDataForAlert(data);
  }

  protected String retrieveFormattedTemperature(WeatherData data) {
    String formattedTemperature;
    Float temperature = data.getTemperature();
    if (temperature != null) {
      formattedTemperature = numberFormat.format(temperature) + "°C";
    } else {
      formattedTemperature = "-";
    }
    return formattedTemperature;
  }

  protected void updateAllWidgets() {
    for (int widgetId : widgetIds) {
      appWidgetManager.updateAppWidget(widgetId, remoteViews);
    }
  }

}
