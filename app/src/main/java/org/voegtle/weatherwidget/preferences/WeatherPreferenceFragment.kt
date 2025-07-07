package org.voegtle.weatherwidget.preferences

import android.os.Build
import android.os.Bundle
import android.preference.PreferenceCategory
import android.preference.PreferenceFragment
import android.preference.PreferenceScreen
import android.view.View
import org.voegtle.weatherwidget.R
import org.voegtle.weatherwidget.util.ContextUtil

class WeatherPreferenceFragment : PreferenceFragment() {
  private val PLACEHOLDER_VERSION = "{v}"
  private val PLACEHOLDER_BUILD = "{b}"

  private var buildNumber: Int = 0
  private var versionName: String = ""

  @Deprecated("Deprecated in Java")
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    addPreferencesFromResource(R.xml.preferences)

    readVersionInformationFromAndroidManifest()
    addBuildInformation(versionNotice)
  }

  override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    patchPaddingForAndroid15(view)
  }

  private fun patchPaddingForAndroid15(view: View?) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
      view?.setPadding(0, 310, 0, 0)
    }
  }

  private fun readVersionInformationFromAndroidManifest() {
    buildNumber = ContextUtil.getBuildNumber(activity)
    versionName = ContextUtil.getVersion(activity)
  }

  private fun addBuildInformation(appInfoScreen: PreferenceScreen) {
    val versionInfo = appInfoScreen.summary.toString()
        .replace(PLACEHOLDER_VERSION, versionName)
        .replace(PLACEHOLDER_BUILD, buildNumber.toString())
    appInfoScreen.summary = versionInfo
    appInfoScreen.getPreference(0).title = versionInfo
  }


  private val versionNotice: PreferenceScreen
    get() {
      val lastIndex = preferenceScreen.preferenceCount - 1

      val versionCategory = preferenceScreen.getPreference(lastIndex) as PreferenceCategory
      return versionCategory.getPreference(0) as PreferenceScreen
    }
}
