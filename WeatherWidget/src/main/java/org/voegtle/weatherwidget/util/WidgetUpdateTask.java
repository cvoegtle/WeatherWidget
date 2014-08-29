package org.voegtle.weatherwidget.util;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import org.voegtle.weatherwidget.R;
import org.voegtle.weatherwidget.data.WeatherData;
import org.voegtle.weatherwidget.location.WeatherLocation;

import java.util.HashMap;
import java.util.List;

public class WidgetUpdateTask extends AbstractWidgetUpdateTask<Void, Void, HashMap<String, WeatherData>> {
  private final List<WeatherLocation> locations;
  private final RemoteViews remoteViews;

  public WidgetUpdateTask(Context context, AppWidgetManager appWidgetManager, int[] widgetIds, RemoteViews remoteViews, List<WeatherLocation> locations) {
    super(context, appWidgetManager, widgetIds, remoteViews);
    this.locations = locations;
    this.remoteViews = remoteViews;
  }


  @Override
  protected HashMap<String, WeatherData> doInBackground(Void... voids) {
    return fetchAllWeatherData();
  }

  @Override
  protected void onPostExecute(HashMap<String, WeatherData> data) {
    try {
      for (WeatherLocation location : locations) {
        visualizeData(location.getWeatherViewId(), location.getShortName(), data.get(location.getKey().toString()));
      }
      checkDataForAlert(data);
    } catch (Throwable th) {
      Log.e(WidgetUpdateTask.class.toString(), "Failed to update View", th);
    } finally {
      remoteViews.setViewVisibility(R.id.refresh_button, View.VISIBLE);
      updateAllWidgets();
    }
  }

  private void visualizeData(int widgetId, String locationName, WeatherData data) {
    remoteViews.setTextColor(widgetId, ColorUtil.byAge(data.getTimestamp()));
    remoteViews.setTextViewText(widgetId, locationName + " "
        + retrieveFormattedTemperature(data));
  }


  @Override
  protected void showDataIsInvalid() {
    remoteViews.setViewVisibility(R.id.refresh_button, View.INVISIBLE);

    for (WeatherLocation location : locations) {
      remoteViews.setTextViewText(location.getWeatherViewId(), location.getShortName() + " " + "-");
    }
    updateAllWidgets();
  }


}
