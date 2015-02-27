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
import org.voegtle.weatherwidget.location.LocationView;
import org.voegtle.weatherwidget.location.WeatherLocation;
import org.voegtle.weatherwidget.preferences.ApplicationSettings;
import org.voegtle.weatherwidget.preferences.ColorScheme;
import org.voegtle.weatherwidget.preferences.WeatherPreferences;
import org.voegtle.weatherwidget.preferences.WeatherSettingsReader;
import org.voegtle.weatherwidget.util.RainUpdater;
import org.voegtle.weatherwidget.util.WeatherDataUpdater;

public class WeatherActivity extends ThemedActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
  private WeatherDataUpdater updater;
  private RainUpdater rainUpdater;
  private ApplicationSettings configuration;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_weather);
    initButton(R.id.button_compare_freiburg_paderborn_bonn, Uri.parse("http://www.voegtle.org/~christian/weather_fr_pb_bn.html"));
    initButton(R.id.button_google_docs, Uri.parse("https://docs.google.com/spreadsheet/ccc?key=0AnsQlmDoHHbKdFVvS1VEMUp6c3FkcElibFhWUGpramc#gid=11"));


    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
    readConfiguration(preferences);
    preferences.registerOnSharedPreferenceChangeListener(this);

    rainUpdater = new RainUpdater(this);

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
          rainUpdater.updateRain(locationView, location.getRainDetailsUrl());
        } else {
          rainUpdater.clearState(locationView);
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
          case R.id.weather_mobil:
            startActivity(new Intent(WeatherActivity.this, MobilDiagramActivity.class));
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

  private void updateRainData() {
    for (WeatherLocation location : configuration.getLocations()) {
      final LocationView locationView = (LocationView) findViewById(location.getWeatherViewId());
      if (locationView.isExpanded()) {
        rainUpdater.updateRain(locationView, location.getRainDetailsUrl());
      }
    }
  }

  private void updateVisibility(WeatherLocation location) {
    boolean show = location.getPreferences().isShowInApp();
    updateVisibility(location.getWeatherViewId(), show);
  }

  private void updateVisibility(int viewId, boolean isVisible) {
    View view = findViewById(viewId);
    view.setVisibility(isVisible ? View.VISIBLE : View.GONE);
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
    rainUpdater.setupRain(locationView, location.getRainDetailsUrl());
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
    updateRainData();
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
