package org.voegtle.weatherwidget.notification;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import org.voegtle.weatherwidget.R;
import org.voegtle.weatherwidget.WeatherActivity;
import org.voegtle.weatherwidget.data.WeatherData;
import org.voegtle.weatherwidget.location.LocationIdentifier;
import org.voegtle.weatherwidget.location.WeatherLocation;
import org.voegtle.weatherwidget.preferences.WeatherActivityConfiguration;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class NotificationSystemManager {
  private static int ALERT_ID = 1;
  private static int INFO_ID = 2;

  private final Resources res;
  private final Context context;
  private WeatherActivityConfiguration configuration;
  private NotificationManager notificationManager;
  private WeatherStationCheck stationCheck;
  private final DecimalFormat numberFormat;


  public NotificationSystemManager(Context context, WeatherActivityConfiguration configuration) {
    this.context = context;
    this.configuration = configuration;
    this.res = context.getResources();
    this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
    this.stationCheck = new WeatherStationCheck(preferences);
    this.numberFormat = (DecimalFormat) NumberFormat.getNumberInstance(Locale.GERMANY);
    this.numberFormat.applyPattern("###.#");
  }

  public void checkDataForAlert(HashMap<LocationIdentifier, WeatherData> data) {
    if (data.size() > 0) {
      showAlertNotification(stationCheck.checkForOverdueStations(data));
      showInfoNotifcation(data);
    }
  }

  private void showAlertNotification(List<WeatherAlert> alerts) {
    if (alerts.size() == 0) {
      notificationManager.cancel(ALERT_ID);
    } else {
      Notification.Builder notificationBuilder = new Notification.Builder(context);
      notificationBuilder.setSmallIcon(R.drawable.wetterlogo);

      Bitmap bm = BitmapFactory.decodeResource(res, R.drawable.wetterlogo_alert);
      notificationBuilder.setLargeIcon(bm);

      notificationBuilder.setContentTitle(res.getString(R.string.data_overdue));

      String contentText = buildMessage(alerts);
      notificationBuilder.setContentText(contentText);

      Intent intentOpenApp = new Intent(context, WeatherActivity.class);
      PendingIntent pendingOpenApp = PendingIntent.getActivity(context, 0, intentOpenApp, PendingIntent.FLAG_UPDATE_CURRENT);
      notificationBuilder.setContentIntent(pendingOpenApp);

      Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
      notificationBuilder.setSound(alarmSound);
      Notification notification = notificationBuilder.build();
      notification.flags |= Notification.FLAG_ONLY_ALERT_ONCE;
      notificationManager.notify(ALERT_ID, notification);
    }
  }

  private String buildMessage(List<WeatherAlert> alerts) {
    StringBuilder contentText = new StringBuilder();
    for (WeatherAlert alert : alerts) {
      contentText.append(alert.getLocation())
          .append(" ").append(res.getString(R.string.since))
          .append(" ").append(formatTime(alert.getLastUpdate()))
          .append(" ").append(res.getString(R.string.overdue)).append(".\n");
    }
    return contentText.substring(0, contentText.length() - 1);
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

  private void showInfoNotifcation(HashMap<LocationIdentifier, WeatherData> data) {
    if (!configuration.isShowInfoNotification()) {
      notificationManager.cancel(INFO_ID);
    } else {
      Notification.Builder notificationBuilder = new Notification.Builder(context);
      notificationBuilder.setSmallIcon(R.drawable.wetterlogo);
      notificationBuilder.setContentTitle(res.getString(R.string.app_name));

      String contentText = buildCurrentWeather(data);
      notificationBuilder.setContentText(contentText);

      Intent intentOpenApp = new Intent(context, WeatherActivity.class);
      PendingIntent pendingOpenApp = PendingIntent.getActivity(context, 0, intentOpenApp, PendingIntent.FLAG_UPDATE_CURRENT);
      notificationBuilder.setContentIntent(pendingOpenApp);

      Notification notification = notificationBuilder.build();
      notification.flags |= Notification.FLAG_ONLY_ALERT_ONCE;
      notification.flags |= Notification.FLAG_ONGOING_EVENT;
      notificationManager.notify(INFO_ID, notification);
    }
  }

  private String buildCurrentWeather(HashMap<LocationIdentifier, WeatherData> data) {
    StringBuilder weatherText = new StringBuilder();

    for (WeatherLocation location : configuration.getLocations()) {
      WeatherData weatherData = data.get(location.getKey());
      if (weatherData != null) {
        weatherText.append(location.getShortName());
        weatherText.append(": ");
        weatherText.append(numberFormat.format(weatherData.getTemperature()) + "°C, ");
        weatherText.append(numberFormat.format(weatherData.getHumidity()) + "%");
        weatherText.append(" | ");
      }
    }

    return weatherText.substring(0, weatherText.length() - 3);
  }


}
