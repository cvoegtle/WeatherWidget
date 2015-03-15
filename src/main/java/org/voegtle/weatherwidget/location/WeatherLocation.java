package org.voegtle.weatherwidget.location;

import android.net.Uri;
import org.voegtle.weatherwidget.preferences.LocationPreferences;

public class WeatherLocation {
  private LocationIdentifier key;
  private String name;
  private String shortName;
  private String identifier;
  private Uri forecastUrl;
  private Uri statisticsUrl;
  private int weatherViewId;
  private int rainIndicatorId;
  private String prefShowInWidget;
  private String prefShowInApp;
  private String prefAlert;
  private boolean visibleInAppByDefault = true;
  private boolean visibleInWidgetByDefault = true;
  private LocationPreferences preferences;
  private int weatherLineId;

  public WeatherLocation(LocationIdentifier key) {
    this.key = key;
  }

  public boolean isActive() {
    return preferences.isShowInApp() || preferences.isShowInWidget() || preferences.isAlertActive();
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

  public int getWeatherViewId() {
    return weatherViewId;
  }

  public void setWeatherViewId(int weatherViewId) {
    this.weatherViewId = weatherViewId;
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

  public Uri getStatisticsUrl() {
    return statisticsUrl;
  }

  public void setStatisticsUrl(Uri statisticsUrl) {
    this.statisticsUrl = statisticsUrl;
  }

  public int getRainIndicatorId() {
    return rainIndicatorId;
  }

  public void setRainIndicatorId(int rainIndicatorId) {
    this.rainIndicatorId = rainIndicatorId;
  }

  public void setWeatherLineId(int weatherLineId) {
    this.weatherLineId = weatherLineId;
  }

  public int getWeatherLineId() {
    return weatherLineId;
  }

  public String getIdentifier() {
    return identifier;
  }

  public void setIdentifier(String identifier) {
    this.identifier = identifier;
  }

  public boolean isVisibleInAppByDefault() {
    return visibleInAppByDefault;
  }

  public void setVisibleInAppByDefault(boolean visibleInAppByDefault) {
    this.visibleInAppByDefault = visibleInAppByDefault;
  }

  public boolean isVisibleInWidgetByDefault() {
    return visibleInWidgetByDefault;
  }

  public void setVisibleInWidgetByDefault(boolean visibleInWidgetByDefault) {
    this.visibleInWidgetByDefault = visibleInWidgetByDefault;
  }
}
