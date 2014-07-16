package org.voegtle.weatherwidget.preferences;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;

public class WeatherPreferences extends Activity {

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    getFragmentManager().beginTransaction()
        .replace(android.R.id.content, new WeatherPreferenceFragment())
        .commit();

    ActionBar actionBar = getActionBar();
    assert actionBar != null;
    actionBar.setDisplayHomeAsUpEnabled(true);
  }

}
