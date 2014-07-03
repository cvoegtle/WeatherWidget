package org.voegtle.weatherwidget.preferences;

public class LocationPreferences {
  boolean showInWidget;
  boolean showInApp;

  public LocationPreferences(boolean showInWidget, boolean showInApp) {

    this.showInWidget = showInWidget;
    this.showInApp = showInApp;
  }

  public boolean isShowInWidget() {
    return showInWidget;
  }

  public boolean isShowInApp() {
    return showInApp;
  }

}
