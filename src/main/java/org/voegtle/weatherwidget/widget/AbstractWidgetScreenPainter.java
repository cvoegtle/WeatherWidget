package org.voegtle.weatherwidget.widget;

import android.appwidget.AppWidgetManager;
import android.widget.RemoteViews;
import org.voegtle.weatherwidget.util.DataFormatter;

public abstract class AbstractWidgetScreenPainter {
  private final AppWidgetManager appWidgetManager;
  private final int[] widgetIds;
  private final RemoteViews remoteViews;
  protected DataFormatter formatter = new DataFormatter();

  protected AbstractWidgetScreenPainter(AppWidgetManager appWidgetManager, int[] widgetIds, RemoteViews remoteViews) {
    this.appWidgetManager = appWidgetManager;
    this.widgetIds = widgetIds;
    this.remoteViews = remoteViews;
  }

  public abstract void showDataIsInvalid();

  public abstract void showDataIsValid();

  protected void updateAllWidgets() {
    for (int widgetId : widgetIds) {
      appWidgetManager.updateAppWidget(widgetId, remoteViews);
    }
  }

}
