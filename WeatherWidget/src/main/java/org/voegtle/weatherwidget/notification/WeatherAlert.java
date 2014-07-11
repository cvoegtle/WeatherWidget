package org.voegtle.weatherwidget.notification;


import java.util.Date;

public class WeatherAlert {
  String location;
  Date lastUpdate;

  public WeatherAlert(String location, Date lastUpdate) {
    this.location = location;
    this.lastUpdate = lastUpdate;
  }

  public String getLocation() {
    return location;
  }

  public Date getLastUpdate() {
    return lastUpdate;
  }
}
