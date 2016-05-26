package org.voegtle.weatherwidget.widget;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import org.voegtle.weatherwidget.R;
import org.voegtle.weatherwidget.preferences.ApplicationSettings;
import org.voegtle.weatherwidget.preferences.WeatherSettingsReader;
import org.voegtle.weatherwidget.system.WidgetUpdateManager;
import org.voegtle.weatherwidget.util.NotificationTask;

import java.util.ArrayList;

public class WidgetRefreshService extends Service implements SharedPreferences.OnSharedPreferenceChangeListener {
  private Resources res;

  private ApplicationSettings configuration;
  private ScreenPainterFactory screenPainterFactory;
  private PowerManager pm;

  @Override
  public void onCreate() {
    super.onCreate();
    ensureResources();

    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    preferences.registerOnSharedPreferenceChangeListener(this);


    pm = (PowerManager) getSystemService(Context.POWER_SERVICE);

    processPreferences(preferences);
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    int result;
    try {
      ensureResources();
      updateWidget();
    } finally {
      result = super.onStartCommand(intent, flags, startId);
    }
    return result;
  }

  @TargetApi(Build.VERSION_CODES.KITKAT_WATCH)
  private void updateWidget() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT_WATCH || pm.isInteractive()) {
      ArrayList<WidgetScreenPainter> screenPainters = screenPainterFactory.createScreenPainters();
      new WidgetUpdateTask(getApplicationContext(), configuration, screenPainters).execute();
    }
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
      String message = getNotificationMessage(interval);
      new NotificationTask(getApplicationContext(), message).execute();
    }

    screenPainterFactory = new ScreenPainterFactory(this, configuration);
  }

  private String getNotificationMessage(Integer interval) {
    String message;
    if (interval > 0) {
      message = getApplicationContext().getString(R.string.intervall_changed) + " " + interval + "min";
    } else {
      message = getApplicationContext().getString(R.string.update_deaktiviert);
    }
    return message;
  }


  private void ensureResources() {
    if (res == null) {
      res = getResources();
      configuration = new ApplicationSettings();
      screenPainterFactory = new ScreenPainterFactory(this, configuration);
    }
  }


  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

}
