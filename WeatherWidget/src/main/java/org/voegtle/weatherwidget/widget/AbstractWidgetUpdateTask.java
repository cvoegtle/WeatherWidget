package org.voegtle.weatherwidget.widget;

import android.content.Context;
import android.os.AsyncTask;
import org.voegtle.weatherwidget.data.WeatherData;
import org.voegtle.weatherwidget.location.LocationIdentifier;
import org.voegtle.weatherwidget.notification.NotificationSystemManager;
import org.voegtle.weatherwidget.util.WeatherDataFetcher;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;

abstract class AbstractWidgetUpdateTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {

  private final Context context;
  private AbstractWidgetScreenPainter screenPainter;
  private final WeatherDataFetcher weatherDataFetcher;

  public AbstractWidgetUpdateTask(Context context, AbstractWidgetScreenPainter screenPainter) {
    super();

    this.context = context;
    this.screenPainter = screenPainter;
    this.weatherDataFetcher = new WeatherDataFetcher();
  }

  @Override
  protected void onPreExecute() {
    screenPainter.showDataIsInvalid();
  }


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


}
