package org.voegtle.weatherwidget.notification;

import android.content.SharedPreferences;
import org.voegtle.weatherwidget.data.WeatherData;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class NotificationBuilder {
  private static int THRESHOLD = 20 * 60 * 1000; // 20 min
  boolean alertPaderborn;
  boolean alertFreiburg;
  boolean alertBonn;

  List<WeatherAlert> alerts = new ArrayList<WeatherAlert>();

  public NotificationBuilder(SharedPreferences preferences) {
    alertPaderborn = preferences.getBoolean("alert_paderborn", false);
    alertFreiburg = preferences.getBoolean("alert_freiburg", false);
    alertBonn = preferences.getBoolean("alert_bonn", false);
  }

  public List<WeatherAlert> buildAlerts(HashMap<String, WeatherData> data) {
    alerts.clear();
    if (alertPaderborn) {
      buildAlert(data.get("Paderborn"));
    }

    if (alertFreiburg) {
      buildAlert(data.get("Freiburg"));
    }
    if (alertBonn) {
      buildAlert(data.get("Bonn"));
    }

    return alerts;
  }

  private void buildAlert(WeatherData data) {
    Date now = new Date();
    if ((now.getTime() - data.getTimestamp().getTime()) > THRESHOLD) {
      alerts.add(new WeatherAlert(data.getLocation(), data.getTimestamp()));
    }
  }
}
