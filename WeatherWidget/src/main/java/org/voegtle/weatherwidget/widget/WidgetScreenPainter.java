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
  private boolean detailed;

  public WidgetScreenPainter(AppWidgetManager appWidgetManager, int[] widgetIds, RemoteViews remoteViews, List<WeatherLocation> locations, boolean detailed) {
    super(appWidgetManager, widgetIds, remoteViews);
    this.remoteViews = remoteViews;
    this.locations = locations;
    this.detailed = detailed;
  }

  @Override
  public void showDataIsInvalid() {
    remoteViews.setViewVisibility(R.id.refresh_button, View.INVISIBLE);

    for (WeatherLocation location : locations) {
      remoteViews.setTextColor(location.getWeatherViewId(), ColorUtil.updateColor());
      remoteViews.setTextColor(location.getRainIndicatorId(), ColorUtil.updateColor());
    }
    updateAllWidgets();
  }

  @Override
  public void showDataIsValid() {
    remoteViews.setViewVisibility(R.id.refresh_button, View.VISIBLE);
    updateAllWidgets();
  }

  public void updateWidgetData(HashMap<LocationIdentifier, WeatherData> data) {
    boolean updated = false;
    for (WeatherLocation location : locations) {
      WeatherData weatherData = data.get(location.getKey());
      updated |= visualizeData(location, weatherData);
    }
    if (updated) {
      updateUpdateTime();
    }
  }

  private void updateUpdateTime() {
    DateFormat df = new SimpleDateFormat("HH:mm");
    remoteViews.setTextViewText(R.id.update_time, df.format(new Date()));
  }

  private boolean visualizeData(WeatherLocation location, WeatherData data) {
    boolean updated;
    if (data == null) {
      remoteViews.setTextColor(location.getWeatherViewId(), ColorUtil.outdatedColor());
      remoteViews.setTextColor(location.getRainIndicatorId(), ColorUtil.outdatedColor());
      updated = false;
    } else {
      remoteViews.setTextColor(location.getWeatherViewId(), ColorUtil.byAge(data.getTimestamp()));
      remoteViews.setTextViewText(location.getWeatherViewId(), getWeatherText(location, data));

      boolean isRaining = data.getRain() != null;
      remoteViews.setTextColor(location.getRainIndicatorId(), ColorUtil.byRain(isRaining, data.getTimestamp()));
      updated = true;
    }
    return updated;
  }

  private String getWeatherText(WeatherLocation location, WeatherData data) {
    StringBuilder weatherData = new StringBuilder(location.getShortName() + " "
        + retrieveFormattedTemperature(data));
    if (detailed) {
      weatherData.append(" | ");
      weatherData.append(numberFormat.format(data.getHumidity()));
      weatherData.append("%");
      if (data.getRainToday() != null) {
        weatherData.append(" | ");
        weatherData.append(numberFormat.format(data.getRainToday()));
        weatherData.append("l");
      }
    }

    return weatherData.toString();
  }

}
