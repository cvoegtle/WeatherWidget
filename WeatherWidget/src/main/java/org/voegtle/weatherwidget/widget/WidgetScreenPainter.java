package org.voegtle.weatherwidget.widget;

import android.appwidget.AppWidgetManager;
import android.view.View;
import android.widget.RemoteViews;
import org.voegtle.weatherwidget.R;
import org.voegtle.weatherwidget.data.WeatherData;
import org.voegtle.weatherwidget.location.LocationIdentifier;
import org.voegtle.weatherwidget.location.WeatherLocation;
import org.voegtle.weatherwidget.util.ColorUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class WidgetScreenPainter extends AbstractWidgetScreenPainter {

  private final RemoteViews remoteViews;
  private List<WeatherLocation> locations;

  public WidgetScreenPainter(AppWidgetManager appWidgetManager, int[] widgetIds, RemoteViews remoteViews, List<WeatherLocation> locations) {
    super(appWidgetManager, widgetIds, remoteViews);
    this.remoteViews = remoteViews;
    this.locations = locations;
  }

  @Override
  public void showDataIsInvalid() {
    remoteViews.setViewVisibility(R.id.refresh_button, View.INVISIBLE);

    for (WeatherLocation location : locations) {
      remoteViews.setTextViewText(location.getWeatherViewId(), location.getShortName() + " " + "-");
    }
    updateAllWidgets();
  }

  @Override
  public void showDataIsValid() {
    remoteViews.setViewVisibility(R.id.refresh_button, View.VISIBLE);
    updateAllWidgets();
  }

  public void updateWidgetData(HashMap<LocationIdentifier, WeatherData> data) {
    for (WeatherLocation location : locations) {
      WeatherData weatherData = data.get(location.getKey());
      visualizeData(location, weatherData);
    }
    updateUpdateTime();
  }

  private void updateUpdateTime() {
    DateFormat df = new SimpleDateFormat("HH:mm");
    remoteViews.setTextViewText(R.id.update_time, df.format(new Date()));
  }

  private void visualizeData(WeatherLocation location, WeatherData data) {
    if (data == null) {
      remoteViews.setTextViewText(location.getWeatherViewId(), location.getShortName());
    } else {
      remoteViews.setTextColor(location.getWeatherViewId(), ColorUtil.byAge(data.getTimestamp()));
      remoteViews.setTextViewText(location.getWeatherViewId(), location.getShortName() + " "
          + retrieveFormattedTemperature(data));

      boolean isRaining = data.getRain() != null;
      remoteViews.setTextColor(location.getRainIndicatorId(), ColorUtil.byRain(isRaining, data.getTimestamp()));
    }
  }

}
