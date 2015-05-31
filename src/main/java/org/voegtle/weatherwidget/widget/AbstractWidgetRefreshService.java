package org.voegtle.weatherwidget.widget;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.TypedValue;
import android.view.View;
import android.widget.RemoteViews;
import org.voegtle.weatherwidget.R;
import org.voegtle.weatherwidget.location.WeatherLocation;
import org.voegtle.weatherwidget.preferences.ApplicationSettings;
import org.voegtle.weatherwidget.preferences.ColorScheme;
import org.voegtle.weatherwidget.preferences.WeatherSettingsReader;
import org.voegtle.weatherwidget.system.IntentFactory;
import org.voegtle.weatherwidget.system.WidgetUpdateManager;
import org.voegtle.weatherwidget.util.NotificationTask;

import static android.os.Build.VERSION.SDK_INT;

public abstract class AbstractWidgetRefreshService extends Service implements SharedPreferences.OnSharedPreferenceChangeListener {
  private Resources res;
  private RemoteViews remoteViews;
  private ApplicationSettings configuration;

  protected abstract Class<?> getWidgetProviderClass();
  protected abstract boolean isDetailed();

  @Override
  public void onCreate() {
    super.onCreate();
    ensureResources();

    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    preferences.registerOnSharedPreferenceChangeListener(this);

    processPreferences(preferences);
    setWidgetIntents();
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    setWidgetIntents();
    updateWidget();
    return super.onStartCommand(intent, flags, startId);
  }

  private void updateWidget() {
    ensureResources();

    ComponentName thisWidget = new ComponentName(this, getWidgetProviderClass());
    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
    int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
    WidgetScreenPainter screenPainter = new WidgetScreenPainter(appWidgetManager, allWidgetIds, remoteViews,
        configuration, getRefreshImage(), isDetailed());
    new WidgetUpdateTask(getApplicationContext(), configuration, screenPainter).execute();
  }

  private Drawable getRefreshImage() {
    return getApplicationContext().getResources().getDrawable(configuration.getColorScheme() == ColorScheme.dark ? R.drawable.ic_action_refresh : R.drawable.ic_action_refresh_dark);
  }


  @Override
  public void onSharedPreferenceChanged(SharedPreferences preferences, String s) {
    processPreferences(preferences);
  }

  @Override
  public void onDestroy() {
    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    preferences.unregisterOnSharedPreferenceChangeListener(this);
    super.onDestroy();
  }

  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    setWidgetIntents();
  }

  private void processPreferences(SharedPreferences preferences) {
    Integer oldInterval = configuration.getUpdateIntervall();

    WeatherSettingsReader weatherSettingsReader = new WeatherSettingsReader(res);
    configuration = weatherSettingsReader.read(preferences);

    Integer interval = configuration.getUpdateIntervall();

    if (oldInterval == null || oldInterval.compareTo(interval) != 0) {
      new WidgetUpdateManager(getApplicationContext()).rescheduleService();
    }

    if (oldInterval != null && oldInterval.compareTo(interval) != 0) {
      String message;
      if (interval > 0) {
        message = getApplicationContext().getString(R.string.intervall_changed) + " " + interval + "min";
      } else {
        message = getApplicationContext().getString(R.string.update_deaktiviert);
      }
      new NotificationTask(getApplicationContext(), message).execute();
    }

    for (WeatherLocation location : configuration.getLocations()) {
      boolean show = location.getPreferences().isShowInWidget();
      updateVisibility(location.getWeatherLineId(), show);
      if (SDK_INT >= 16) {
        remoteViews.setTextViewTextSize(location.getWeatherViewId(), TypedValue.COMPLEX_UNIT_SP, configuration.getWidgetTextSize());
      }
    }

    updateAllWidgets();
  }

  private void updateVisibility(int id, boolean isVisible) {
    remoteViews.setViewVisibility(id, isVisible ? View.VISIBLE : View.GONE);
  }

  protected void updateAllWidgets() {
    updateBackgroundColor();
    ComponentName thisWidget = new ComponentName(this, getWidgetProviderClass());
    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
    int[] widgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

    for (int widgetId : widgetIds) {
      appWidgetManager.updateAppWidget(widgetId, remoteViews);
    }
  }

  private void updateBackgroundColor() {
    if (configuration.getColorScheme().equals(ColorScheme.dark)) {
      remoteViews.setInt(R.id.widget_container, "setBackgroundColor", Color.argb(0xB1, 0x00, 0x00, 0x00));
    } else {
      remoteViews.setInt(R.id.widget_container, "setBackgroundColor", Color.argb(0xD0, 0xff, 0xff, 0xff));
    }
  }


  private void setWidgetIntents() {
    PendingIntent pendingOpenApp = IntentFactory.createOpenAppIntent(getApplicationContext());
    for (WeatherLocation location : configuration.getLocations()) {
      remoteViews.setOnClickPendingIntent(location.getWeatherViewId(), pendingOpenApp);
    }

    remoteViews.setOnClickPendingIntent(R.id.refresh_button, IntentFactory.createRefreshIntent(getApplicationContext(), this.getClass()));
    updateAllWidgets();
  }

  private void ensureResources() {
    if (res == null) {
      res = getResources();
      configuration = new ApplicationSettings();
      remoteViews = new RemoteViews(getPackageName(), R.layout.widget_weather);
    }
  }

  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

}
