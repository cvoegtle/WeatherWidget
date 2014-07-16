package org.voegtle.weatherwidget.preferences;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import org.voegtle.weatherwidget.R;

public class WeatherPreferenceFragment extends PreferenceFragment {
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    addPreferencesFromResource(R.xml.preferences);
  }

}
