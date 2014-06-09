package org.voegtle.weatherwidget;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import org.voegtle.weatherwidget.util.WeatherDataUpdater;

public class WeatherActivity extends Activity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_weather);

    initButton(R.id.button_compare_freiburg_paderborn, "http://www.voegtle.org/~christian/weather_fr_pb_bn.html");
    initButton(R.id.button_google_docs, "https://docs.google.com/spreadsheet/ccc?key=0AnsQlmDoHHbKdFVvS1VEMUp6c3FkcElibFhWUGpramc#gid=11");
    initButton(R.id.button_forecast_freiburg, "http://wetterstationen.meteomedia.de/?station=108030&wahl=vorhersage");
    initButton(R.id.button_forecast_bonn, "http://wetterstationen.meteomedia.de/?station=105170&wahl=vorhersage");
    initButton(R.id.button_forecast_paderborn, "http://wetterstationen.meteomedia.de/?station=104300&wahl=vorhersage");

    WeatherDataUpdater updater =  new WeatherDataUpdater(this, getResources());
    updater.startWeatherScheduler();
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
  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
  }
}
