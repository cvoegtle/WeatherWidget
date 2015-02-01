package org.voegtle.weatherwidget.widget;

import android.content.Context;
import android.os.AsyncTask;
import org.voegtle.weatherwidget.data.WeatherData;
import org.voegtle.weatherwidget.location.LocationIdentifier;
import org.voegtle.weatherwidget.notification.NotificationSystemManager;
import org.voegtle.weatherwidget.preferences.ApplicationSettings;
import org.voegtle.weatherwidget.util.WeatherDataFetcher;

import java.util.HashMap;

abstract class AbstractWidgetUpdateTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {

  private final Context context;
  private ApplicationSettings configuration;
  private AbstractWidgetScreenPainter screenPainter;
  private final WeatherDataFetcher weatherDataFetcher;

  public AbstractWidgetUpdateTask(Context context, ApplicationSettings configuration,
                                  AbstractWidgetScreenPainter screenPainter) {
    super();

    this.context = context;
    this.configuration = configuration;
    this.screenPainter = screenPainter;
    this.weatherDataFetcher = new WeatherDataFetcher();
  }

  @Override
  protected void onPreExecute() {
    screenPainter.showDataIsInvalid();
    screenPainter.updateAllWidgets();
  }


  protected HashMap<LocationIdentifier, WeatherData> fetchAllWeatherData() {
    return weatherDataFetcher.fetchAllWeatherDataFromServer(configuration.getLocations(), configuration.getSecret());
  }

  protected WeatherData fetchWeatherData(String weatherServerUrl) {
    return weatherDataFetcher.fetchWeatherDataFromUrl(weatherServerUrl);
  }

  protected void checkDataForAlert(HashMap<LocationIdentifier, WeatherData> data) {
    NotificationSystemManager notificationManager = new NotificationSystemManager(context, configuration);
    notificationManager.checkDataForAlert(data);
  }


}
