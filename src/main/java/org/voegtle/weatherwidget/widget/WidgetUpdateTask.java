package org.voegtle.weatherwidget.widget;

import android.content.Context;
import android.util.Log;
import org.voegtle.weatherwidget.data.WeatherData;
import org.voegtle.weatherwidget.location.LocationIdentifier;
import org.voegtle.weatherwidget.preferences.ApplicationSettings;

import java.util.ArrayList;
import java.util.HashMap;

public class WidgetUpdateTask extends AbstractWidgetUpdateTask<Void, Void, HashMap<LocationIdentifier, WeatherData>> {
  private final ArrayList<WidgetScreenPainter> screenPainters;

  public WidgetUpdateTask(Context context, ApplicationSettings configuration, ArrayList<WidgetScreenPainter> screenPainters) {
    super(context, configuration);
    this.screenPainters = screenPainters;
  }

  @Override
  protected void onPreExecute() {
    for (WidgetScreenPainter screenPainter : screenPainters) {
      screenPainter.showDataIsInvalid();
      screenPainter.updateAllWidgets();
    }
  }


  @Override
  protected HashMap<LocationIdentifier, WeatherData> doInBackground(Void... voids) {
    try {
      if (!screenPainters.isEmpty()) {
        return fetchAllWeatherData();
      }
    } catch (Throwable ignore) {
    }
    return new HashMap<>();
  }

  protected HashMap<LocationIdentifier, WeatherData> fetchAllWeatherData() {
    return weatherDataFetcher.fetchAllWeatherDataFromServer(configuration.getLocations(), configuration.getSecret());
  }


  @Override
  protected void onPostExecute(HashMap<LocationIdentifier, WeatherData> data) {
    try {
      for (WidgetScreenPainter screenPainter : screenPainters) {
        screenPainter.updateWidgetData(data);
      }
      checkDataForAlert(data);
    } catch (Throwable th) {
      Log.e(WidgetUpdateTask.class.toString(), "Failed to update View", th);
    } finally {
      showDataIsValid();
    }
  }

  private void showDataIsValid() {
    try {
      for (WidgetScreenPainter screenPainter : screenPainters) {
        screenPainter.showDataIsValid();
      }
    } catch (Throwable th) {
      Log.e(WidgetUpdateTask.class.toString(), "Failed to repaint view", th);
    }
  }
}
