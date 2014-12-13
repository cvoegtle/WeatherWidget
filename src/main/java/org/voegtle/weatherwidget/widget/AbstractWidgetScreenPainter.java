package org.voegtle.weatherwidget.widget;

import android.appwidget.AppWidgetManager;
import android.widget.RemoteViews;
import org.voegtle.weatherwidget.data.WeatherData;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public abstract class AbstractWidgetScreenPainter {
  private final AppWidgetManager appWidgetManager;
  private final int[] widgetIds;
  private final RemoteViews remoteViews;
  protected final DecimalFormat numberFormat;

  protected AbstractWidgetScreenPainter(AppWidgetManager appWidgetManager, int[] widgetIds, RemoteViews remoteViews) {
    this.appWidgetManager = appWidgetManager;
    this.widgetIds = widgetIds;
    this.remoteViews = remoteViews;

    this.numberFormat = (DecimalFormat) NumberFormat.getNumberInstance(Locale.GERMANY);
    this.numberFormat.applyPattern("###.#");
  }

  public abstract void showDataIsInvalid();

  public abstract void showDataIsValid();

  protected void updateAllWidgets() {
    for (int widgetId : widgetIds) {
      appWidgetManager.updateAppWidget(widgetId, remoteViews);
    }
  }

  protected String retrieveFormattedTemperature(WeatherData data) {
    String formattedTemperature;
    Float temperature = data.getTemperature();
    if (temperature != null) {
      formattedTemperature = numberFormat.format(temperature) + "°C";
    } else {
      formattedTemperature = "-";
    }
    return formattedTemperature;
  }

}
