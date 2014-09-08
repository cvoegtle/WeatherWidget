package org.voegtle.weatherwidget.system;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import org.voegtle.weatherwidget.preferences.WeatherSettingsReader;

public class WidgetUpdateManager {

  private AlarmManager alarmManager;
  private PendingIntent refreshService;
  private Integer interval;


  public WidgetUpdateManager(Context context) {
    alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    refreshService = IntentFactory.createRefreshIntent(context);

    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
    processPreferences(preferences);
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


  public void processPreferences(SharedPreferences preferences) {
    WeatherSettingsReader weatherSettingsReader = new WeatherSettingsReader();

    Integer oldInterval = interval;
    interval = weatherSettingsReader.readIntervall(preferences);
  }

}
