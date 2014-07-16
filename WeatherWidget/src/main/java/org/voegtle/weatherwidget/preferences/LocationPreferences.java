package org.voegtle.weatherwidget.preferences;

public class LocationPreferences {
  boolean showInWidget;
  boolean showInApp;
  boolean alertActive;

  public LocationPreferences(boolean showInWidget, boolean showInApp, boolean alertActive) {

    this.showInWidget = showInWidget;
    this.showInApp = showInApp;
    this.alertActive = alertActive;
  }

  public boolean isShowInWidget() {
    return showInWidget;
  }

  public boolean isShowInApp() {
    return showInApp;
  }

  public boolean isAlertActive() {
    return alertActive;
  }
}
