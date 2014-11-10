package org.voegtle.weatherwidget.preferences;

import android.content.SharedPreferences;
import org.voegtle.weatherwidget.location.WeatherLocation;

import java.util.List;

public class WeatherSettingsReader {

  public WeatherSettingsReader() {
  }

  public void read(SharedPreferences preferences, List<WeatherLocation> locations) {

    for (WeatherLocation location : locations) {
      LocationPreferences locationPreferences = new LocationPreferences(getBoolean(preferences, location.getPrefShowInWidget()),
          getBoolean(preferences, location.getPrefShowInApp()),
          getBoolean(preferences, location.getPrefAlert(), false));
      location.setPreferences(locationPreferences);
    }

  }


  private Integer getInteger(SharedPreferences preferences, String key, int defaultValue) {
    String value = preferences.getString(key, Integer.toString(defaultValue));
    return new Integer(value);
  }

  private boolean getBoolean(SharedPreferences preferences, String key) {
    return getBoolean(preferences, key, true);
  }

  private boolean getBoolean(SharedPreferences preferences, String key, boolean defaultValue) {
    return preferences.getBoolean(key, defaultValue);
  }

  public String getString(SharedPreferences preferences, String key) {
    return preferences.getString(key, "");
  }

  public int readIntervall(SharedPreferences preferences) {
    return getInteger(preferences, "update_interval", 30);
  }
}
