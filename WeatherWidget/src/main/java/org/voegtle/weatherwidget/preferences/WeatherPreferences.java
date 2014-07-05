package org.voegtle.weatherwidget.preferences;

import android.app.ActionBar;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import org.voegtle.weatherwidget.R;

public class WeatherPreferences extends PreferenceActivity {

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    addPreferencesFromResource(R.xml.preferences);

    ActionBar actionBar = getActionBar();
    actionBar.setDisplayHomeAsUpEnabled(true);

  }

}
