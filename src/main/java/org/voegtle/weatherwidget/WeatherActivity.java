package org.voegtle.weatherwidget;

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
import org.voegtle.weatherwidget.base.ThemedActivity;
import org.voegtle.weatherwidget.diagram.*;
import org.voegtle.weatherwidget.location.LocationIdentifier;
import org.voegtle.weatherwidget.location.LocationView;
import org.voegtle.weatherwidget.location.WeatherLocation;
import org.voegtle.weatherwidget.preferences.ApplicationSettings;
import org.voegtle.weatherwidget.preferences.ColorScheme;
import org.voegtle.weatherwidget.preferences.WeatherPreferences;
import org.voegtle.weatherwidget.preferences.WeatherSettingsReader;
import org.voegtle.weatherwidget.util.StatisticsUpdater;
import org.voegtle.weatherwidget.util.WeatherDataUpdater;

import java.util.HashMap;

public class WeatherActivity extends ThemedActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
  private WeatherDataUpdater updater;
  private StatisticsUpdater statisticsUpdater;
  private ApplicationSettings configuration;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_weather);
    initButton(R.id.button_google_docs, Uri.parse("https://docs.google.com/spreadsheets/d/1ahkm9SDTqjYcsLgKIH5yjmqlAh6dKxgfIrZA5Dt9L3o/edit?usp=sharing"));

    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
    readConfiguration(preferences);
    preferences.registerOnSharedPreferenceChangeListener(this);

    statisticsUpdater = new StatisticsUpdater(this);

    setupLocations();
    configureLocationSymbolColor();

    updater = new WeatherDataUpdater(this, configuration);
  }

  private void startWeatherUpdater() {
    updater.startWeatherScheduler(180);
  }

  private void setupLocations() {
    for (WeatherLocation location : configuration.getLocations()) {
      addClickHandler(location);
      updateVisibility(location);
      updateState(location);
      updateTextSize(location, configuration.getAppTextSize());
    }
  }

  private void readConfiguration(SharedPreferences preferences) {
    WeatherSettingsReader weatherSettingsReader = new WeatherSettingsReader(this.getApplicationContext());
    configuration = weatherSettingsReader.read(preferences);
  }

  private void addClickHandler(final WeatherLocation location) {
    final LocationView locationView = (LocationView) findViewById(location.getWeatherViewId());
    locationView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (locationView.isExpanded()) {
          statisticsUpdater.updateStatistics(locationView, location);
        } else {
          statisticsUpdater.clearState(locationView);
        }
      }
    });

    locationView.setDiagramsOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        switch (view.getId()) {
          case R.id.weather_paderborn:
            startActivity(new Intent(WeatherActivity.this, PaderbornDiagramActivity.class));
            break;
          case R.id.weather_bali:
            startActivity(new Intent(WeatherActivity.this, BaliDiagramActivity.class));
            break;
          case R.id.weather_bonn:
            startActivity(new Intent(WeatherActivity.this, BonnDiagramActivity.class));
            break;
          case R.id.weather_freiburg:
            startActivity(new Intent(WeatherActivity.this, FreiburgDiagramActivity.class));
            break;
          case R.id.weather_leo:
            startActivity(new Intent(WeatherActivity.this, LeoDiagramActivity.class));
            break;
          case R.id.weather_herzogenaurach:
            startActivity(new Intent(WeatherActivity.this, HerzoDiagramActivity.class));
            break;
          case R.id.weather_mobil:
            Intent intent = new Intent(WeatherActivity.this, MobilDiagramActivity.class);
            WeatherLocation location = configuration.findLocation(LocationIdentifier.Mobil);
            if (location != null) {
              intent.putExtra(MobilDiagramActivity.class.getName(), location.getName());
            }
            startActivity(intent);
            break;
        }
      }
    });

    locationView.setForecastOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, location.getForecastUrl());
        startActivity(browserIntent);
      }
    });

  }

  private void updateStatistics() {
    HashMap<LocationView, WeatherLocation> updateCandidates = new HashMap<>();
    for (WeatherLocation location : configuration.getLocations()) {
      final LocationView locationView = (LocationView) findViewById(location.getWeatherViewId());
      if (locationView.isExpanded()) {
        updateCandidates.put(locationView, location);
      }
    }
    statisticsUpdater.updateStatistics(updateCandidates, false);
  }

  private void updateVisibility(WeatherLocation location) {
    boolean show = location.getPreferences().isShowInApp();
    updateVisibility(location.getWeatherViewId(), show);
  }

  private void updateVisibility(int viewId, boolean isVisible) {
    View view = findViewById(viewId);
    view.setVisibility(isVisible ? View.VISIBLE : View.GONE);
  }

  private void updateTextSize(WeatherLocation location, Integer textSize) {
    if (location.getPreferences().isShowInApp()) {
      LocationView view = (LocationView) findViewById(location.getWeatherViewId());
      view.setTextSize(textSize);
    }
  }

  private void configureLocationSymbolColor() {
    // Dunkle Symbole wenn der Hintergrund hell ist
    boolean darkSymbols = getColorScheme() == ColorScheme.light;
    for (WeatherLocation location : configuration.getLocations()) {
      final LocationView locationView = (LocationView) findViewById(location.getWeatherViewId());
      locationView.configureSymbols(darkSymbols);
    }

  }

  private void updateState(WeatherLocation location) {
    final LocationView locationView = (LocationView) findViewById(location.getWeatherViewId());
    statisticsUpdater.setupStatistics(locationView);
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

  @Override
  protected void onResume() {
    super.onResume();
    updateStatistics();
    updater.updateWeatherOnce(false);
    startWeatherUpdater();
  }

  @Override
  protected void onPause() {
    super.onPause();
    updater.stopWeatherScheduler();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.weather_activity_menu, menu);
    return super.onCreateOptionsMenu(menu);

  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_reload:
        updater.updateWeatherOnce(true);
        return true;
      case R.id.action_diagrams:
        startActivity(new Intent(this, MainDiagramActivity.class));
        return true;
      case R.id.action_perferences:
        startActivity(new Intent(this, WeatherPreferences.class));
        return true;
    }
    return false;
  }

  @Override
  public void onSharedPreferenceChanged(SharedPreferences preferences, String s) {
    readConfiguration(preferences);
    setupLocations();
    updater.stopWeatherScheduler();
    updater = new WeatherDataUpdater(this, configuration);
  }
}
