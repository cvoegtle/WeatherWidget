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
import org.voegtle.weatherwidget.diagram.FreiburgDiagramActivity;
import org.voegtle.weatherwidget.diagram.MainDiagramActivity;
import org.voegtle.weatherwidget.diagram.PaderbornDiagramActivity;
import org.voegtle.weatherwidget.location.LocationFactory;
import org.voegtle.weatherwidget.location.LocationView;
import org.voegtle.weatherwidget.location.WeatherLocation;
import org.voegtle.weatherwidget.preferences.WeatherActivityConfiguration;
import org.voegtle.weatherwidget.preferences.WeatherPreferences;
import org.voegtle.weatherwidget.preferences.WeatherSettingsReader;
import org.voegtle.weatherwidget.util.RainUpdater;
import org.voegtle.weatherwidget.util.WeatherDataUpdater;

public class WeatherActivity extends Activity implements SharedPreferences.OnSharedPreferenceChangeListener {
  private WeatherDataUpdater updater;
  private RainUpdater rainUpdater;
  private WeatherActivityConfiguration configuration = new WeatherActivityConfiguration();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_weather);
    configuration.setLocations(LocationFactory.buildWeatherLocations(this.getResources()));

    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
    preferences.registerOnSharedPreferenceChangeListener(this);

    rainUpdater = new RainUpdater(this);
    configure(preferences);

    initButton(R.id.button_compare_freiburg_paderborn_bonn, Uri.parse("http://www.voegtle.org/~christian/weather_fr_pb_bn.html"));
    initButton(R.id.button_google_docs, Uri.parse("https://docs.google.com/spreadsheet/ccc?key=0AnsQlmDoHHbKdFVvS1VEMUp6c3FkcElibFhWUGpramc#gid=11"));

    updater = new WeatherDataUpdater(this, configuration);
  }

  private void startWeatherUpdater() {
    updater.startWeatherScheduler(180);
  }

  private void configure(SharedPreferences preferences) {
    WeatherSettingsReader weatherSettings = new WeatherSettingsReader();
    weatherSettings.read(preferences, configuration.getLocations());
    configuration.setSecret(weatherSettings.getString(preferences, "secret"));

    for (WeatherLocation location : configuration.getLocations()) {
      addClickHandler(location);
      updateVisibility(location);
      updateState(location);
    }
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
          case R.id.weather_freiburg:
            startActivity(new Intent(WeatherActivity.this, FreiburgDiagramActivity.class));
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

  private void updateVisibility(WeatherLocation location) {
    boolean show = location.getPreferences().isShowInApp();
    updateVisibility(location.getWeatherViewId(), show);
  }

  private void updateState(WeatherLocation location) {
    final LocationView locationView = (LocationView) findViewById(location.getWeatherViewId());
    rainUpdater.setupRain(locationView, location.getRainDetailsUrl());
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

  @Override
  protected void onResume() {
    super.onResume();
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
    configure(preferences);
  }
}
