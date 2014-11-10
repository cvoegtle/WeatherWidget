package org.voegtle.weatherwidget.notification;

import android.content.SharedPreferences;
import org.voegtle.weatherwidget.data.WeatherData;
import org.voegtle.weatherwidget.location.LocationIdentifier;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class NotificationBuilder {
  private static int THRESHOLD = 20 * 60 * 1000; // 20 min
  boolean alertPaderborn;
  boolean alertFreiburg;
  boolean alertBonn;

  List<WeatherAlert> alerts = new ArrayList<>();

  public NotificationBuilder(SharedPreferences preferences) {
    alertPaderborn = preferences.getBoolean("alert_paderborn", false);
    alertFreiburg = preferences.getBoolean("alert_freiburg", false);
    alertBonn = preferences.getBoolean("alert_bonn", false);
  }

  public List<WeatherAlert> buildAlerts(HashMap<LocationIdentifier, WeatherData> data) {
    alerts.clear();
    if (alertPaderborn) {
      buildAlert(data.get(LocationIdentifier.Paderborn));
    }

    if (alertFreiburg) {
      buildAlert(data.get(LocationIdentifier.Freiburg));
    }
    if (alertBonn) {
      buildAlert(data.get(LocationIdentifier.Bonn));
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
