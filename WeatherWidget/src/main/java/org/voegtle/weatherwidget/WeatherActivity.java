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
import org.voegtle.weatherwidget.preferences.WeatherPreferences;
import org.voegtle.weatherwidget.preferences.WeatherSettings;
import org.voegtle.weatherwidget.util.WeatherDataUpdater;

public class WeatherActivity extends Activity implements SharedPreferences.OnSharedPreferenceChangeListener {
  private WeatherDataUpdater updater;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_weather);

    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
    preferences.registerOnSharedPreferenceChangeListener(this);

    setupUserInterface(preferences);

    initButton(R.id.button_compare_freiburg_paderborn, "http://www.voegtle.org/~christian/weather_fr_pb_bn.html");
    initButton(R.id.button_google_docs, "https://docs.google.com/spreadsheet/ccc?key=0AnsQlmDoHHbKdFVvS1VEMUp6c3FkcElibFhWUGpramc#gid=11");
    initButton(R.id.button_forecast_freiburg, "http://wetterstationen.meteomedia.de/?station=108030&wahl=vorhersage");
    initButton(R.id.button_forecast_bonn, "http://wetterstationen.meteomedia.de/?station=105170&wahl=vorhersage");
    initButton(R.id.button_forecast_paderborn, "http://wetterstationen.meteomedia.de/?station=104300&wahl=vorhersage");

    updater = new WeatherDataUpdater(this);
    setupWeatherUpdater(preferences);
  }

  private void setupWeatherUpdater(SharedPreferences preferences) {
    int intervall = Integer.valueOf(preferences.getString("update_interval", "-1"));
    if (intervall > 0) {
      updater.startWeatherScheduler(intervall);
    } else {
      updater.stopWeatherScheduler();
    }
  }

  private void setupUserInterface(SharedPreferences preferences) {
    WeatherSettings weatherSettings = new WeatherSettings(preferences);

    boolean showPaderborn = weatherSettings.getPaderborn().isShowInApp();
    uddateVisibility(findViewById(R.id.caption_paderborn), showPaderborn);
    uddateVisibility(findViewById(R.id.weather_paderborn), showPaderborn);
    uddateVisibility(findViewById(R.id.button_forecast_paderborn), showPaderborn);

    boolean showFreiburg = weatherSettings.getFreiburg().isShowInApp();
    uddateVisibility(findViewById(R.id.caption_freiburg), showFreiburg);
    uddateVisibility(findViewById(R.id.weather_freiburg), showFreiburg);
    uddateVisibility(findViewById(R.id.button_forecast_freiburg), showFreiburg);

    boolean showBonn = weatherSettings.getBonn().isShowInApp();
    uddateVisibility(findViewById(R.id.caption_bonn), showBonn);
    uddateVisibility(findViewById(R.id.weather_bonn), showBonn);
    uddateVisibility(findViewById(R.id.button_forecast_bonn), showBonn);

  }

  private void uddateVisibility(View view, boolean isVisible) {
    view.setVisibility(isVisible ? View.VISIBLE : View.GONE);
  }

  private void initButton(int buttonId, final String url) {
    Button button = (Button) findViewById(buttonId);

    button.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View arg0) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
      }

    });
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
        updater.updateWeatherOnce();
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
