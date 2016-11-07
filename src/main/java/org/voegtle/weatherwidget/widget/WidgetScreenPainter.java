package org.voegtle.weatherwidget.widget;

import android.appwidget.AppWidgetManager;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.RemoteViews;
import org.voegtle.weatherwidget.R;
import org.voegtle.weatherwidget.data.WeatherData;
import org.voegtle.weatherwidget.location.LocationIdentifier;
import org.voegtle.weatherwidget.location.WeatherLocation;
import org.voegtle.weatherwidget.preferences.ApplicationSettings;
import org.voegtle.weatherwidget.preferences.ColorScheme;
import org.voegtle.weatherwidget.util.ColorUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class WidgetScreenPainter extends AbstractWidgetScreenPainter {

  private final RemoteViews remoteViews;
  private final ColorScheme colorScheme;
  private List<WeatherLocation> locations;
  private boolean detailed;

  public WidgetScreenPainter(AppWidgetManager appWidgetManager, int[] widgetIds, RemoteViews remoteViews, ApplicationSettings configuration, Drawable refreshImage, boolean detailed) {
    super(appWidgetManager, widgetIds, remoteViews);
    this.remoteViews = remoteViews;
    this.locations = configuration.getLocations();
    this.colorScheme = configuration.getColorScheme();
    this.detailed = detailed;
    remoteViews.setBitmap(R.id.refresh_button, "setImageBitmap", ((BitmapDrawable) refreshImage).getBitmap());
  }

  @Override
  public void showDataIsInvalid() {
    remoteViews.setViewVisibility(R.id.refresh_button, View.GONE);

    for (WeatherLocation location : locations) {
      remoteViews.setTextColor(location.getWeatherViewId(), ColorUtil.updateColor(colorScheme));
      remoteViews.setTextColor(location.getRainIndicatorId(), ColorUtil.updateColor(colorScheme));
    }
    remoteViews.setTextColor(R.id.update_time, ColorUtil.updateColor(colorScheme));
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
    DateFormat df = new SimpleDateFormat("HH:mm", Locale.GERMANY);
    remoteViews.setTextViewText(R.id.update_time, df.format(new Date()));
    remoteViews.setTextColor(R.id.update_time, ColorUtil.byAge(colorScheme, new Date()));
  }

  private boolean visualizeData(WeatherLocation location, WeatherData data) {
    boolean updated;
    if (data == null) {
      remoteViews.setTextColor(location.getWeatherViewId(), ColorUtil.outdatedColor(colorScheme));
      remoteViews.setTextColor(location.getRainIndicatorId(), ColorUtil.outdatedColor(colorScheme));
      updated = false;
    } else {
      remoteViews.setTextColor(location.getWeatherViewId(), ColorUtil.byAge(colorScheme, data.getTimestamp()));
      remoteViews.setTextViewText(location.getWeatherViewId(), formatter.formatWidgetLine(location, data, detailed));

      remoteViews.setTextColor(location.getRainIndicatorId(), ColorUtil.byRain(data.isRaining(), colorScheme, data.getTimestamp()));
      updated = true;
    }
    return updated;
  }

}
