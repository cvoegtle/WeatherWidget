package org.voegtle.weatherwidget.widget;

import android.app.Service;
import android.content.*;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.hardware.display.DisplayManager;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.view.Display;
import org.voegtle.weatherwidget.R;
import org.voegtle.weatherwidget.preferences.ApplicationSettings;
import org.voegtle.weatherwidget.preferences.WeatherSettingsReader;
import org.voegtle.weatherwidget.system.WidgetUpdateManager;
import org.voegtle.weatherwidget.util.NotificationTask;

import java.util.ArrayList;
import java.util.Date;

public class WidgetRefreshService extends Service implements SharedPreferences.OnSharedPreferenceChangeListener {
  private Resources res;

  private ApplicationSettings configuration;
  private ScreenPainterFactory screenPainterFactory;
  private Long lastUpdate;

  @Override
  public void onCreate() {
    super.onCreate();
    ensureResources();

    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    preferences.registerOnSharedPreferenceChangeListener(this);

    processPreferences(preferences);

    IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
    registerReceiver(new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent intent) {
        if (isLastUpdateOutdated()) {
          ensureResources();
          updateWidget();
        }
      }
    }, filter);
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    int result;
    try {
      if (isScreenOn()) {
        ensureResources();
        updateWidget();
      }
    } finally {
      result = super.onStartCommand(intent, flags, startId);
    }
    return result;
  }

  private void updateWidget() {
    lastUpdate = new Date().getTime();
    ArrayList<WidgetScreenPainter> screenPainters = screenPainterFactory.createScreenPainters();
    new WidgetUpdateTask(getApplicationContext(), configuration, screenPainters).execute();
  }

  public boolean isScreenOn() {
    Context context = getApplicationContext();
    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
      DisplayManager dm = (DisplayManager) context.getSystemService(Context.DISPLAY_SERVICE);
      boolean screenOn = false;
      for (Display display : dm.getDisplays()) {
        if (display.getState() != Display.STATE_OFF) {
          screenOn = true;
        }
      }
      return screenOn;
    } else {
      PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
      //noinspection deprecation
      return pm.isScreenOn();
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

  static private long WAITING_PERIOD = 5 * 60 * 1000;
  private boolean isLastUpdateOutdated() {
    return lastUpdate == null || (new Date().getTime()-lastUpdate) > WAITING_PERIOD;
  }


  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

}
