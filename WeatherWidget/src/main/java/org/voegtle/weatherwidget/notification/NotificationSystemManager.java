package org.voegtle.weatherwidget.notification;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import org.voegtle.weatherwidget.R;
import org.voegtle.weatherwidget.WeatherActivity;
import org.voegtle.weatherwidget.data.WeatherData;
import org.voegtle.weatherwidget.location.LocationIdentifier;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class NotificationSystemManager {
  private static int NOTIFICATION_ID = 1;
  private final Resources res;
  private final Context context;
  private NotificationManager notificationManager;
  private NotificationBuilder notificationBuilder;

  public NotificationSystemManager(Context context) {
    this.context = context;
    this.res = context.getResources();
    this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    this.notificationBuilder = new NotificationBuilder(PreferenceManager.getDefaultSharedPreferences(context));
  }

  public void checkDataForAlert(HashMap<LocationIdentifier, WeatherData> data) {
    if (data.size() > 0) {
      showNotification(notificationBuilder.buildAlerts(data));
    }
  }

  private void showNotification(List<WeatherAlert> alerts) {
    if (alerts.size() == 0) {
      notificationManager.cancel(NOTIFICATION_ID);
    } else {
      Notification.Builder notificationBuilder = new Notification.Builder(context);
      notificationBuilder.setSmallIcon(R.drawable.wetterlogo);
      notificationBuilder.setContentTitle(res.getString(R.string.data_overdue));

      StringBuilder contentText = new StringBuilder();
      for (WeatherAlert alert : alerts) {
        contentText.append(alert.getLocation())
            .append(" ").append(res.getString(R.string.since))
            .append(" ").append(formatTime(alert.getLastUpdate()))
            .append(" ").append(res.getString(R.string.overdue)).append(".\n");
      }
      notificationBuilder.setContentText(contentText.substring(0, contentText.length() - 1));

      Intent intentOpenApp = new Intent(context, WeatherActivity.class);
      PendingIntent pendingOpenApp = PendingIntent.getActivity(context, 0, intentOpenApp, PendingIntent.FLAG_UPDATE_CURRENT);
      notificationBuilder.setContentIntent(pendingOpenApp);

      Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
      notificationBuilder.setSound(alarmSound);
      Notification notification = notificationBuilder.build();
      notification.flags |= Notification.FLAG_ONLY_ALERT_ONCE;
      notificationManager.notify(NOTIFICATION_ID, notification);
    }
  }

  private String formatTime(Date lastUpdate) {
    long timeInMinutes = (new Date().getTime() - lastUpdate.getTime()) / (60 * 1000);
    if (timeInMinutes < 120) {
      return timeInMinutes + " " + res.getString(R.string.minutes);
    }
    if (timeInMinutes < (48 * 60)) {
      return (timeInMinutes / 60) + " " + res.getString(R.string.hours);
    }

    return (timeInMinutes / (24 * 60)) + " " + res.getString(R.string.days);
  }
}
