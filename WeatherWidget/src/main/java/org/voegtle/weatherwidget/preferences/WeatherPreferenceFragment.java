package org.voegtle.weatherwidget.preferences;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import org.voegtle.weatherwidget.R;

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

  private void addBuildInformation(Preference versionNotice) {
    String summary = versionNotice.getSummary().toString();
    summary = summary.replace(PLACEHOLDER_VERSION, versionName);
    summary = summary.replace(PLACEHOLDER_BUILD, Integer.toString(buildNumber));
    versionNotice.setSummary(summary);
  }


  private Preference getVersionNotice() {
    int lastIndex = getPreferenceScreen().getPreferenceCount() - 1;

    PreferenceCategory versionCategory = (PreferenceCategory) getPreferenceScreen().getPreference(lastIndex);
    return versionCategory.getPreference(0);
  }

  private void readVersionInformationFromAndroidManifest() {
    try {
      PackageManager packageManager = getActivity().getPackageManager();
      String packageName = getActivity().getPackageName();
      buildNumber = packageManager.getPackageInfo(packageName, 0).versionCode;
      versionName = packageManager.getPackageInfo(packageName, 0).versionName;
    } catch (Exception ignored) {
    }
  }

}
