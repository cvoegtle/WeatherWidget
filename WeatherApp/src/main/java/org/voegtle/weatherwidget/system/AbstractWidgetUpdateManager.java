package org.voegtle.weatherwidget.system;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import org.voegtle.weatherwidget.preferences.WeatherActivityConfiguration;
import org.voegtle.weatherwidget.preferences.WeatherSettingsReader;

public abstract class AbstractWidgetUpdateManager {

  private AlarmManager alarmManager;
  private PendingIntent refreshService;
  private Integer interval;

  protected AbstractWidgetUpdateManager(Context context, Class<?> cls) {
    alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    refreshService = IntentFactory.createRefreshIntent(context, cls);

    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
    processPreferences(preferences, context);
  }

  public void runServiceNow() {
    alarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + 10, refreshService);
  }

  public void rescheduleService() {
    cancelAlarmService();

    if (interval != null && interval > 0) {
      alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 10, interval * 60 * 1000, refreshService);
    }
  }

  public void cancelAlarmService() {
    alarmManager.cancel(refreshService);
  }


  public void processPreferences(SharedPreferences preferences, Context context) {
    WeatherSettingsReader weatherSettingsReader = new WeatherSettingsReader(context);
    WeatherActivityConfiguration configuration = weatherSettingsReader.read(preferences);
    interval = configuration.getUpdateIntervall();
  }


}
