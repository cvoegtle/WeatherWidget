package org.voegtle.weatherwidget.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import org.voegtle.weatherwidget.location.LocationFactory;
import org.voegtle.weatherwidget.location.WeatherLocation;

import java.util.List;

import static org.voegtle.weatherwidget.preferences.ColorScheme.byKey;

public class WeatherSettingsReader {

  private Resources resources;

  public WeatherSettingsReader(Context context) {
    this.resources = context.getResources();
  }

  public WeatherSettingsReader(Resources resources) {
    this.resources = resources;
  }

  public ApplicationSettings read(SharedPreferences preferences) {
    ApplicationSettings configuration = new ApplicationSettings();
    List<WeatherLocation> locations = readLocations(preferences);
    configuration.setLocations(locations);
    configuration.setSecret(getString(preferences, "secret"));
    configuration.setShowInfoNotification(getBoolean(preferences, "info_notification", false));
    configuration.setUpdateIntervall(getInteger(preferences, "update_interval", 30));
    configuration.setColorScheme(getColorScheme(preferences, "color_scheme", ColorScheme.dark));

    return configuration;
  }

  private ColorScheme getColorScheme(SharedPreferences preferences, String key, ColorScheme defaultScheme) {
    String value = getString(preferences, key);
    ColorScheme scheme = byKey(value);
    if (scheme == null) {
      scheme = defaultScheme;
    }
    return scheme;
  }

  private List<WeatherLocation> readLocations(SharedPreferences preferences) {
    List<WeatherLocation> locations = LocationFactory.buildWeatherLocations(resources);
    for (WeatherLocation location : locations) {
      LocationPreferences locationPreferences = new LocationPreferences(getBoolean(preferences, location.getPrefShowInWidget()),
          getBoolean(preferences, location.getPrefShowInApp()),
          getBoolean(preferences, location.getPrefAlert(), false));
      location.setPreferences(locationPreferences);
    }
    return locations;
  }


  private Integer getInteger(SharedPreferences preferences, String key, int defaultValue) {
    String value = preferences.getString(key, Integer.toString(defaultValue));
    return Integer.valueOf(value);
  }

  private boolean getBoolean(SharedPreferences preferences, String key) {
    return getBoolean(preferences, key, true);
  }

  private boolean getBoolean(SharedPreferences preferences, String key, boolean defaultValue) {
    return preferences.getBoolean(key, defaultValue);
  }

  private String getString(SharedPreferences preferences, String key) {
    return preferences.getString(key, "");
  }

}
