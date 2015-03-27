package org.voegtle.weatherwidget.preferences;

import org.voegtle.weatherwidget.location.LocationIdentifier;
import org.voegtle.weatherwidget.location.WeatherLocation;

import java.util.List;

public class ApplicationSettings {
  private List<WeatherLocation> locations;
  private String secret;
  private Integer updateIntervall;
  private Integer textSize;
  private boolean showInfoNotification;
  private ColorScheme colorScheme;

  public ApplicationSettings() {
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

  public Integer getUpdateIntervall() {
    return updateIntervall;
  }

  public void setUpdateIntervall(Integer updateIntervall) {
    this.updateIntervall = updateIntervall;
  }

  public boolean isShowInfoNotification() {
    return showInfoNotification;
  }

  public void setShowInfoNotification(boolean showInfoNotification) {
    this.showInfoNotification = showInfoNotification;
  }

  public ColorScheme getColorScheme() {
    return colorScheme;
  }

  public void setColorScheme(ColorScheme colorScheme) {
    this.colorScheme = colorScheme;
  }

  public Integer getWidgetTextSize() {
    return textSize;
  }

  public Integer getAppTextSize() {
    return textSize + textSize / 4;
  }

  public void setTextSize(Integer textSize) {
    this.textSize = textSize;
  }

  public WeatherLocation findLocation(LocationIdentifier identifier) {
    for (WeatherLocation location : locations) {
      if (location.getKey().equals(identifier)) {
        return location;
      }
    }
    return null;
  }
}
