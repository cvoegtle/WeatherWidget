package org.voegtle.weatherwidget.widget;

import android.appwidget.AppWidgetManager;
import android.widget.RemoteViews;
import org.voegtle.weatherwidget.R;
import org.voegtle.weatherwidget.data.WeatherData;
import org.voegtle.weatherwidget.util.ColorUtil;

public class SmallWidgetScreenPainter extends AbstractWidgetScreenPainter {
  private RemoteViews remoteViews;

  public SmallWidgetScreenPainter(AppWidgetManager appWidgetManager, int[] widgetIds, RemoteViews remoteViews) {
    super(appWidgetManager, widgetIds, remoteViews);
    this.remoteViews = remoteViews;
  }

  @Override
  public void showDataIsInvalid() {
    remoteViews.setTextColor(R.id.weather_small, ColorUtil.updateColor());
  }

  @Override
  public void showDataIsValid() {
    updateAllWidgets();
  }

  public void updateData(WeatherData data) {
    if (data != null) {
      remoteViews.setTextViewText(R.id.weather_small, retrieveFormattedTemperature(data));
      remoteViews.setTextColor(R.id.weather_small, ColorUtil.byAge(data.getTimestamp()));
    } else {
      remoteViews.setTextColor(R.id.weather_small, ColorUtil.outdatedColor());
    }
  }
}
