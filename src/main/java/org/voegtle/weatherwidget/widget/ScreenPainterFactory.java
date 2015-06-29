package org.voegtle.weatherwidget.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.View;
import android.widget.RemoteViews;
import org.voegtle.weatherwidget.R;
import org.voegtle.weatherwidget.WeatherWidgetProvider;
import org.voegtle.weatherwidget.WeatherWidgetProviderLarge;
import org.voegtle.weatherwidget.location.WeatherLocation;
import org.voegtle.weatherwidget.preferences.ApplicationSettings;
import org.voegtle.weatherwidget.preferences.ColorScheme;
import org.voegtle.weatherwidget.system.IntentFactory;

import java.util.ArrayList;

import static android.os.Build.VERSION.SDK_INT;

public class ScreenPainterFactory {

  private Context context;
  private ApplicationSettings configuration;

  public ScreenPainterFactory(Context context, ApplicationSettings configuration) {
    this.context = context.getApplicationContext();
    this.configuration = configuration;
  }

  public ArrayList<WidgetScreenPainter> createScreenPainters() {
    ArrayList<WidgetScreenPainter> screenPainters = new ArrayList<>();
    getWidgetScreenPainter(screenPainters, false, WeatherWidgetProvider.class);
    getWidgetScreenPainter(screenPainters, true, WeatherWidgetProviderLarge.class);
    return screenPainters;
  }

  private void getWidgetScreenPainter(ArrayList<WidgetScreenPainter> screenPainters, boolean isDetailed, Class<?> clazz) {
    ComponentName thisWidget = new ComponentName(context, clazz);
    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
    int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
    if (allWidgetIds.length > 0) {
      screenPainters.add(new WidgetScreenPainter(appWidgetManager, allWidgetIds, createRemoteViews(),
          configuration, getRefreshImage(), isDetailed));
    }
  }

  private Drawable getRefreshImage() {
    return ContextCompat.getDrawable(context, configuration.getColorScheme() == ColorScheme.dark ? R.drawable.ic_action_refresh : R.drawable.ic_action_refresh_dark);
  }

  private RemoteViews createRemoteViews() {
    RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_weather);

    updateBackgroundColor(remoteViews);
    for (WeatherLocation location : configuration.getLocations()) {
      boolean show = location.getPreferences().isShowInWidget();
      updateVisibility(remoteViews, location.getWeatherLineId(), show);
      if (SDK_INT >= 16) {
        remoteViews.setTextViewTextSize(location.getWeatherViewId(), TypedValue.COMPLEX_UNIT_SP, configuration.getWidgetTextSize());
      }
    }

    setWidgetIntents(remoteViews);
    return remoteViews;
  }

  private void updateVisibility(RemoteViews remoteViews, int id, boolean isVisible) {
    remoteViews.setViewVisibility(id, isVisible ? View.VISIBLE : View.GONE);
  }


  private void updateBackgroundColor(RemoteViews remoteViews) {
    if (configuration.getColorScheme().equals(ColorScheme.dark)) {
      remoteViews.setInt(R.id.widget_container, "setBackgroundColor", Color.argb(0xB1, 0x00, 0x00, 0x00));
    } else {
      remoteViews.setInt(R.id.widget_container, "setBackgroundColor", Color.argb(0xD0, 0xff, 0xff, 0xff));
    }
  }


  private void setWidgetIntents(RemoteViews remoteViews) {
    PendingIntent pendingOpenApp = IntentFactory.createOpenAppIntent(context);
    for (WeatherLocation location : configuration.getLocations()) {
      remoteViews.setOnClickPendingIntent(location.getWeatherViewId(), pendingOpenApp);
    }

    remoteViews.setOnClickPendingIntent(R.id.refresh_button, IntentFactory.createRefreshIntent(context, WidgetRefreshService.class));
  }


}
