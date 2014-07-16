package org.voegtle.weatherwidget;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import org.voegtle.weatherwidget.location.LocationFactory;
import org.voegtle.weatherwidget.location.WeatherLocation;
import org.voegtle.weatherwidget.preferences.WeatherPreferences;
import org.voegtle.weatherwidget.preferences.WeatherSettingsReader;
import org.voegtle.weatherwidget.util.WeatherDataUpdater;

import java.util.List;

public class WeatherActivity extends Activity implements SharedPreferences.OnSharedPreferenceChangeListener {
  private WeatherDataUpdater updater;
  private List<WeatherLocation> locations;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_weather);
    locations = LocationFactory.buildWeatherLocations(this.getResources());

    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
    preferences.registerOnSharedPreferenceChangeListener(this);

    setupUserInterface(preferences);

    initButton(R.id.button_compare_freiburg_paderborn_bonn, Uri.parse("http://www.voegtle.org/~christian/weather_fr_pb_bn.html"));
    initButton(R.id.button_google_docs, Uri.parse("https://docs.google.com/spreadsheet/ccc?key=0AnsQlmDoHHbKdFVvS1VEMUp6c3FkcElibFhWUGpramc#gid=11"));
    initForecastButtons();

    updater = new WeatherDataUpdater(this, locations);
    setupWeatherUpdater(preferences);
  }

  private void setupWeatherUpdater(SharedPreferences preferences) {
    WeatherSettingsReader weatherSettingsReader = new WeatherSettingsReader();

    int intervall = weatherSettingsReader.readIntervall(preferences);
    if (intervall > 0) {
      updater.startWeatherScheduler(intervall);
    } else {
      updater.stopWeatherScheduler();
    }
  }

  private void setupUserInterface(SharedPreferences preferences) {
    WeatherSettingsReader weatherSettings = new WeatherSettingsReader();
    weatherSettings.read(preferences, locations);

    for (WeatherLocation location : locations) {
      updateVisibility(location);
    }
  }

  private void updateVisibility(WeatherLocation location) {
    boolean show = location.getPreferences().isShowInApp();
    updateVisibility(location.getWeatherCaptionId(), show);
    updateVisibility(location.getWeatherViewId(), show);
    updateVisibility(location.getForecastButtonId(), show);
  }

  private void updateVisibility(int viewId, boolean isVisible) {
    View view = findViewById(viewId);
    view.setVisibility(isVisible ? View.VISIBLE : View.GONE);
  }

  private void initButton(int buttonId, final Uri uri) {
    Button button = (Button) findViewById(buttonId);

    button.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View arg0) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(browserIntent);
      }

    });
  }

  private void initForecastButtons() {
    for (WeatherLocation location : locations) {
      initButton(location.getForecastButtonId(), location.getForecastUrl());
    }
  }

  @Override
  protected void onResume() {
    super.onResume();
    updater.updateWeatherOnce(false);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.main_menu, menu);
    return super.onCreateOptionsMenu(menu);

  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_reload:
        updater.updateWeatherOnce(true);
        break;
      case R.id.action_perferences:
        startActivity(new Intent(this, WeatherPreferences.class));
        return true;
    }
    return false;
  }

  @Override
  public void onSharedPreferenceChanged(SharedPreferences preferences, String s) {
    setupUserInterface(preferences);
    setupWeatherUpdater(preferences);
  }
}
