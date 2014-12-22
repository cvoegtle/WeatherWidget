package org.voegtle.weatherwidget.preferences;

import android.app.ActionBar;
import android.os.Bundle;
import org.voegtle.weatherwidget.base.ThemedActivity;

public class WeatherPreferences extends ThemedActivity {

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
