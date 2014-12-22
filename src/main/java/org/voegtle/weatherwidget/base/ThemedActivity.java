package org.voegtle.weatherwidget.base;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import org.voegtle.weatherwidget.preferences.ApplicationSettings;
import org.voegtle.weatherwidget.preferences.ColorScheme;
import org.voegtle.weatherwidget.preferences.WeatherSettingsReader;

public abstract class ThemedActivity extends Activity {
  private ColorScheme colorScheme = ColorScheme.dark;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    configureTheme();
    super.onCreate(savedInstanceState);
  }

  private void configureTheme() {
    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
    WeatherSettingsReader weatherSettingsReader = new WeatherSettingsReader(this.getApplicationContext());
    ApplicationSettings configuration = weatherSettingsReader.read(preferences);
    colorScheme = configuration.getColorScheme();
    setTheme(colorScheme.getTheme());
  }

  public ColorScheme getColorScheme() {
    return colorScheme;
  }
}
