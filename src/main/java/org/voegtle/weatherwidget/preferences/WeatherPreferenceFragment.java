package org.voegtle.weatherwidget.preferences;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import org.voegtle.weatherwidget.R;
import org.voegtle.weatherwidget.util.ContextUtil;

public class WeatherPreferenceFragment extends PreferenceFragment {
  private static final String PLACEHOLDER_VERSION = "{v}";
  private static final String PLACEHOLDER_BUILD = "{b}";
  private int buildNumber;
  private String versionName;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    addPreferencesFromResource(R.xml.preferences);

    readVersionInformationFromAndroidManifest();
    addBuildInformation(getVersionNotice());

  }

  private void addBuildInformation(PreferenceScreen appInfoScreen) {
    String versionInfo = appInfoScreen.getSummary().toString();
    versionInfo = versionInfo.replace(PLACEHOLDER_VERSION, versionName);
    versionInfo = versionInfo.replace(PLACEHOLDER_BUILD, Integer.toString(buildNumber));
    appInfoScreen.setSummary(versionInfo);

    appInfoScreen.getPreference(0).setTitle(versionInfo);
  }


  private PreferenceScreen getVersionNotice() {
    int lastIndex = getPreferenceScreen().getPreferenceCount() - 1;

    PreferenceCategory versionCategory = (PreferenceCategory) getPreferenceScreen().getPreference(lastIndex);
    return (PreferenceScreen) versionCategory.getPreference(0);
  }

  private void readVersionInformationFromAndroidManifest() {
    try {
      buildNumber = ContextUtil.getBuildNumber(getActivity());
      versionName = ContextUtil.getVersion(getActivity());
    } catch (Exception ignored) {
    }
  }

}
