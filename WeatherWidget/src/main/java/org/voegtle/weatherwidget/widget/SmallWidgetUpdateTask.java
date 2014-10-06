package org.voegtle.weatherwidget.widget;

import android.content.Context;
import android.util.Log;
import org.voegtle.weatherwidget.R;
import org.voegtle.weatherwidget.data.WeatherData;
import org.voegtle.weatherwidget.util.ColorUtil;
import org.voegtle.weatherwidget.util.UserFeedbackWidget;

public class SmallWidgetUpdateTask extends AbstractWidgetUpdateTask<String, Void, WeatherData> {

  private final SmallWidgetScreenPainter screenPainter;

  public SmallWidgetUpdateTask(Context context, SmallWidgetScreenPainter screenPainter) {
    super(context, screenPainter);
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
