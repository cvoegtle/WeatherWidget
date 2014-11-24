package org.voegtle.weatherwidget.preferences;

import org.voegtle.weatherwidget.location.WeatherLocation;

import java.util.List;

public class WeatherActivityConfiguration {
  private List<WeatherLocation> locations;
  private String secret;
  private int updateIntervall;
  private boolean showInfoNotification;

  public WeatherActivityConfiguration() {
  }

  public void setLocations(List<WeatherLocation> locations) {
    this.locations = locations;
  }

  public void setSecret(String secret) {
    this.secret = secret;
  }

  public List<WeatherLocation> getLocations() {
    return locations;
  }

  public String getSecret() {
    return secret;
  }

  public int getUpdateIntervall() {
    return updateIntervall;
  }

  public void setUpdateIntervall(int updateIntervall) {
    this.updateIntervall = updateIntervall;
  }

  public boolean isShowInfoNotification() {
    return showInfoNotification;
  }

  public void setShowInfoNotification(boolean showInfoNotification) {
    this.showInfoNotification = showInfoNotification;
  }

}
