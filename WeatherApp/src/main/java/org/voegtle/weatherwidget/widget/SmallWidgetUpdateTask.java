package org.voegtle.weatherwidget.widget;

import android.content.Context;
import android.util.Log;
import org.voegtle.weatherwidget.data.WeatherData;
import org.voegtle.weatherwidget.preferences.WeatherActivityConfiguration;

public class SmallWidgetUpdateTask extends AbstractWidgetUpdateTask<String, Void, WeatherData> {

  private final SmallWidgetScreenPainter screenPainter;

  public SmallWidgetUpdateTask(Context context, WeatherActivityConfiguration configuration,
                               SmallWidgetScreenPainter screenPainter) {
    super(context, configuration, screenPainter);
    this.screenPainter = screenPainter;
  }


  @Override
  protected WeatherData doInBackground(String... weatherServerUrl) {
    return fetchWeatherData(weatherServerUrl[0]);
  }

  @Override
  protected void onPostExecute(WeatherData data) {
    try {
      screenPainter.updateData(data);
    } catch (Throwable th) {
      Log.e(SmallWidgetUpdateTask.class.toString(), "Failed to update View", th);
    } finally {
      screenPainter.showDataIsValid();
    }

  }
}
