package org.voegtle.weatherwidget.notification;


import org.voegtle.weatherwidget.location.LocationIdentifier;

import java.util.Date;

public class WeatherAlert {
  LocationIdentifier location;
  Date lastUpdate;

  public WeatherAlert(LocationIdentifier location, Date lastUpdate) {
    this.location = location;
    this.lastUpdate = lastUpdate;
  }

  public LocationIdentifier getLocation() {
    return location;
  }

  public Date getLastUpdate() {
    return lastUpdate;
  }
}
