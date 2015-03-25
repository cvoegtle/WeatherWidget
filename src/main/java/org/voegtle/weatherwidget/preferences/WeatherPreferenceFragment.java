package org.voegtle.weatherwidget.preferences;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import org.voegtle.weatherwidget.R;

import static android.os.Build.VERSION.SDK_INT;

public class WeatherPreferenceFragment extends PreferenceFragment {
  private static final String PLACEHOLDER_VERSION = "{v}";
  private static final String PLACEHOLDER_BUILD = "{b}";
  private static final String KEY_FONTSIZE = "widget_font_size";
  private int buildNumber;
  private String versionName;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    addPreferencesFromResource(R.xml.preferences);

    readVersionInformationFromAndroidManifest();
    addBuildInformation(getVersionNotice());
    removeUnsupportedSettings();

  }

  private void removeUnsupportedSettings() {
    if (SDK_INT < 16) {
      Preference preferenceFontSize = getPreferenceScreen().findPreference(KEY_FONTSIZE);
      getPreferenceScreen().removePreference(preferenceFontSize);
    }
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
      PackageManager packageManager = getActivity().getPackageManager();
      String packageName = getActivity().getPackageName();
      buildNumber = packageManager.getPackageInfo(packageName, 0).versionCode;
      versionName = packageManager.getPackageInfo(packageName, 0).versionName;
    } catch (Exception ignored) {
    }
  }

}
