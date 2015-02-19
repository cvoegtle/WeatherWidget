package org.voegtle.weatherwidget.notification;

import org.voegtle.weatherwidget.data.WeatherData;
import org.voegtle.weatherwidget.location.LocationIdentifier;
import org.voegtle.weatherwidget.location.WeatherLocation;
import org.voegtle.weatherwidget.preferences.ApplicationSettings;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class WeatherStationCheck {
  private static int THRESHOLD = 20 * 60 * 1000; // 20 min
  private final ApplicationSettings configuration;

  List<WeatherAlert> alerts = new ArrayList<>();

  public WeatherStationCheck(ApplicationSettings configuration) {
    this.configuration = configuration;
  }

  public List<WeatherAlert> checkForOverdueStations(HashMap<LocationIdentifier, WeatherData> data) {
    alerts.clear();
    for (WeatherLocation location : configuration.getLocations()) {
      if (location.getPreferences().isAlertActive()) {
        buildAlert(data.get(location.getKey()));
      }
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
