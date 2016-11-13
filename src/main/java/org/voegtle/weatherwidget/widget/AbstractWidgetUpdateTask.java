package org.voegtle.weatherwidget.widget;

import android.content.Context;
import android.os.AsyncTask;
import org.voegtle.weatherwidget.data.WeatherData;
import org.voegtle.weatherwidget.location.LocationIdentifier;
import org.voegtle.weatherwidget.notification.NotificationSystemManager;
import org.voegtle.weatherwidget.preferences.ApplicationSettings;
import org.voegtle.weatherwidget.util.ContextUtil;
import org.voegtle.weatherwidget.util.WeatherDataFetcher;

import java.util.HashMap;

abstract class AbstractWidgetUpdateTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {

  private final Context context;
  protected ApplicationSettings configuration;
  protected final WeatherDataFetcher weatherDataFetcher;

  public AbstractWidgetUpdateTask(Context context, ApplicationSettings configuration) {
    super();

    this.context = context;
    this.configuration = configuration;
    this.weatherDataFetcher = new WeatherDataFetcher(ContextUtil.getBuildNumber(context));
  }

  protected WeatherData fetchWeatherData(String weatherServerUrl) {
    return weatherDataFetcher.fetchWeatherDataFromUrl(weatherServerUrl);
  }

  protected void checkDataForAlert(HashMap<LocationIdentifier, WeatherData> data) {
    NotificationSystemManager notificationManager = new NotificationSystemManager(context, configuration);
    notificationManager.checkDataForAlert(data);
  }


}
