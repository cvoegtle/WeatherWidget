package org.voegtle.weatherwidget.widget;

import android.content.Context;
import android.util.Log;
import org.voegtle.weatherwidget.data.WeatherData;
import org.voegtle.weatherwidget.location.LocationIdentifier;
import org.voegtle.weatherwidget.preferences.ApplicationSettings;

import java.util.HashMap;

public class WidgetUpdateTask extends AbstractWidgetUpdateTask<Void, Void, HashMap<LocationIdentifier, WeatherData>> {
  private final WidgetScreenPainter screenPainter;

  public WidgetUpdateTask(Context context, ApplicationSettings configuration, WidgetScreenPainter screenPainter) {
    super(context, configuration, screenPainter);
    this.screenPainter = screenPainter;
  }


  @Override
  protected HashMap<LocationIdentifier, WeatherData> doInBackground(Void... voids) {
    try {
      return fetchAllWeatherData();
    } catch (Throwable th) {
      screenPainter.showDataIsValid();
    }
    return new HashMap<>();
  }

  @Override
  protected void onPostExecute(HashMap<LocationIdentifier, WeatherData> data) {
    try {
      screenPainter.updateWidgetData(data);
      checkDataForAlert(data);
    } catch (Throwable th) {
      Log.e(WidgetUpdateTask.class.toString(), "Failed to update View", th);
    } finally {
      screenPainter.showDataIsValid();
    }
  }
}
