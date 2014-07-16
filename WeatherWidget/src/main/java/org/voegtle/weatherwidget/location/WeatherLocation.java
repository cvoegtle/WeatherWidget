package org.voegtle.weatherwidget.location;

import android.net.Uri;
import org.voegtle.weatherwidget.preferences.LocationPreferences;

public class WeatherLocation {
  private LocationIdentifier key;
  private String name;
  private String shortName;
  private Uri forecastUrl;
  private int weatherCaptionId;
  private int weatherViewId;
  private int forecastButtonId;
  private String prefShowInWidget;
  private String prefShowInApp;
  private String prefAlert;
  private LocationPreferences preferences;

  public WeatherLocation(LocationIdentifier key) {
    this.key = key;
  }

  public LocationIdentifier getKey() {
    return key;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getShortName() {
    return shortName;
  }

  public void setShortName(String shortName) {
    this.shortName = shortName;
  }

  public Uri getForecastUrl() {
    return forecastUrl;
  }

  public void setForecastUrl(Uri forecastUrl) {
    this.forecastUrl = forecastUrl;
  }

  public int getWeatherCaptionId() {
    return weatherCaptionId;
  }

  public void setWeatherCaptionId(int weatherCaptionId) {
    this.weatherCaptionId = weatherCaptionId;
  }

  public int getWeatherViewId() {
    return weatherViewId;
  }

  public void setWeatherViewId(int weatherViewId) {
    this.weatherViewId = weatherViewId;
  }

  public int getForecastButtonId() {
    return forecastButtonId;
  }

  public void setForecastButtonId(int forecastButtonId) {
    this.forecastButtonId = forecastButtonId;
  }

  public LocationPreferences getPreferences() {
    return preferences;
  }

  public String getPrefShowInWidget() {
    return prefShowInWidget;
  }

  public void setPrefShowInWidget(String prefShowInWidget) {
    this.prefShowInWidget = prefShowInWidget;
  }

  public String getPrefShowInApp() {
    return prefShowInApp;
  }

  public void setPrefShowInApp(String prefShowInApp) {
    this.prefShowInApp = prefShowInApp;
  }

  public String getPrefAlert() {
    return prefAlert;
  }

  public void setPrefAlert(String prefAlert) {
    this.prefAlert = prefAlert;
  }

  public void setPreferences(LocationPreferences preferences) {
    this.preferences = preferences;
  }
}
