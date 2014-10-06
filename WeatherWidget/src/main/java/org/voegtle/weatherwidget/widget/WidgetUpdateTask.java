package org.voegtle.weatherwidget.widget;

import android.content.Context;
import android.util.Log;
import org.voegtle.weatherwidget.data.WeatherData;
import org.voegtle.weatherwidget.location.LocationIdentifier;

import java.util.HashMap;

public class WidgetUpdateTask extends AbstractWidgetUpdateTask<Void, Void, HashMap<LocationIdentifier, WeatherData>> {
  private final WidgetScreenPainter screenPainter;

  public WidgetUpdateTask(Context context, WidgetScreenPainter screenPainter) {
    super(context, screenPainter);
    this.screenPainter = screenPainter;
  }


  @Override
  protected HashMap<LocationIdentifier, WeatherData> doInBackground(Void... voids) {
    return fetchAllWeatherData();
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
