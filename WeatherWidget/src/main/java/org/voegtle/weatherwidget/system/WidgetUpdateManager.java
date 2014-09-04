package org.voegtle.weatherwidget.system;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import org.voegtle.weatherwidget.R;
import org.voegtle.weatherwidget.preferences.WeatherSettingsReader;
import org.voegtle.weatherwidget.util.NotificationTask;

public class WidgetUpdateManager {

  private AlarmManager alarmManager;
  private PendingIntent refreshService;
  private int intervall = -1;
  private Context context;


  public WidgetUpdateManager(Context context) {
    this.context = context;
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

    String message;
    if (intervall > 0) {
      alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 10, intervall * 60 * 1000, refreshService);
      message = context.getResources().getString(R.string.intervall_changed) + " " + intervall + "min";
    } else {
      message = context.getString(R.string.update_deaktiviert);
    }

    new NotificationTask(context, message).execute();
  }

  public void cancelAlarmService() {
    if (refreshService != null) {
      alarmManager.cancel(refreshService);
    }
  }


  /**
   * @return true if preferences changed
   */
  public boolean processPreferences(SharedPreferences preferences) {
    WeatherSettingsReader weatherSettingsReader = new WeatherSettingsReader();

    int oldIntervall = intervall;
    intervall = weatherSettingsReader.readIntervall(preferences);

    return oldIntervall != intervall;
  }

}
