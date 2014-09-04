package org.voegtle.weatherwidget.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.util.Log;
import android.widget.RemoteViews;
import org.voegtle.weatherwidget.R;
import org.voegtle.weatherwidget.data.WeatherData;
import org.voegtle.weatherwidget.util.ColorUtil;
import org.voegtle.weatherwidget.util.UserFeedbackWidget;

public class SmallWidgetUpdateTask extends AbstractWidgetUpdateTask<String, Void, WeatherData> {
  private final RemoteViews remoteViews;
  private final Context context;

  public SmallWidgetUpdateTask(Context context, AppWidgetManager appWidgetManager, int[] widgetIds, RemoteViews remoteViews) {
    super(context, appWidgetManager, widgetIds, remoteViews);
    this.context = context;
    this.remoteViews = remoteViews;
  }

  @Override
  protected void showDataIsInvalid() {
    remoteViews.setTextViewText(R.id.weather_small, "-");
  }

  @Override
  protected WeatherData doInBackground(String... weatherServerUrl) {
    return fetchWeatherData(weatherServerUrl[0]);
  }

  @Override
  protected void onPostExecute(WeatherData data) {
    try {
      remoteViews.setTextViewText(R.id.weather_small, retrieveFormattedTemperature(data));
      remoteViews.setTextColor(R.id.weather_small, ColorUtil.byAge(data.getTimestamp()));
      new UserFeedbackWidget(context).showMessage(R.string.message_data_updated);
    } catch (Throwable th) {
      Log.e(SmallWidgetUpdateTask.class.toString(), "Failed to update View", th);
    } finally {
      updateAllWidgets();
    }

  }
}
