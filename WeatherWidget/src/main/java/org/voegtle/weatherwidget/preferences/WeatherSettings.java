package org.voegtle.weatherwidget.preferences;

import android.content.SharedPreferences;

public class WeatherSettings {
  private LocationPreferences paderborn;
  private LocationPreferences bonn;
  private LocationPreferences freiburg;

  private int intervall;


  public WeatherSettings(SharedPreferences preferences) {
    paderborn = new LocationPreferences(getBoolean(preferences, "widget_show_paderborn"), getBoolean(preferences, "app_show_paderborn"));
    freiburg = new LocationPreferences(getBoolean(preferences, "widget_show_freiburg"), getBoolean(preferences, "app_show_freiburg"));
    bonn = new LocationPreferences(getBoolean(preferences, "widget_show_bonn"), getBoolean(preferences, "app_show_bonn"));

    intervall = getInteger(preferences, "update_interval");
  }

  public LocationPreferences getPaderborn() {
    return paderborn;
  }

  public LocationPreferences getBonn() {
    return bonn;
  }

  public LocationPreferences getFreiburg() {
    return freiburg;
  }

  public int getIntervall() {
    return intervall;
  }

  private int getInteger(SharedPreferences preferences, String key) {
    String value = preferences.getString(key, "-1");
    return new Integer(value);
  }

  private boolean getBoolean(SharedPreferences preferences, String key) {
    return preferences.getBoolean(key, true);
  }
}
