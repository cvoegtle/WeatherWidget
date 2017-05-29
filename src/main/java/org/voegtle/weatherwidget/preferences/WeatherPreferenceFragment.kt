package org.voegtle.weatherwidget.preferences

import android.os.Bundle
import android.preference.PreferenceCategory
import android.preference.PreferenceFragment
import android.preference.PreferenceScreen
import org.voegtle.weatherwidget.R
import org.voegtle.weatherwidget.util.ContextUtil

class WeatherPreferenceFragment : PreferenceFragment() {
  private val PLACEHOLDER_VERSION = "{v}"
  private val PLACEHOLDER_BUILD = "{b}"

  private var buildNumber: Int = 0
  private var versionName: String = ""

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    addPreferencesFromResource(R.xml.preferences)

    readVersionInformationFromAndroidManifest()
    addBuildInformation(versionNotice)

  }

  private fun addBuildInformation(appInfoScreen: PreferenceScreen) {
    var versionInfo = appInfoScreen.summary.toString()
    versionInfo = versionInfo.replace(PLACEHOLDER_VERSION, versionName)
    versionInfo = versionInfo.replace(PLACEHOLDER_BUILD, Integer.toString(buildNumber))
    appInfoScreen.summary = versionInfo

    appInfoScreen.getPreference(0).title = versionInfo
  }


  private val versionNotice: PreferenceScreen
    get() {
      val lastIndex = preferenceScreen.preferenceCount - 1

      val versionCategory = preferenceScreen.getPreference(lastIndex) as PreferenceCategory
      return versionCategory.getPreference(0) as PreferenceScreen
    }

  private fun readVersionInformationFromAndroidManifest() {
    try {
      buildNumber = ContextUtil.getBuildNumber(activity)
      versionName = ContextUtil.getVersion(activity)
    } catch (ignored: Exception) {
    }

  }
}
