package org.voegtle.weatherwidget.preferences

import android.os.Bundle
import androidx.preference.Preference // AndroidX Import
import androidx.preference.PreferenceFragmentCompat // AndroidX Import
import androidx.preference.PreferenceScreen // AndroidX Import
import org.voegtle.weatherwidget.R
import org.voegtle.weatherwidget.util.ContextUtil

class WeatherPreferenceFragment : PreferenceFragmentCompat() {
    private val PLACEHOLDER_VERSION = "{v}"
    private val PLACEHOLDER_BUILD = "{b}"

    private val VERSION_INFO_KEY = "version" // Passe diesen Key ggf. an!

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        var versionName = ""
        var buildNumber = 0

        try {
            val activity = requireActivity()
            versionName = ContextUtil.getVersion(activity)
            buildNumber = ContextUtil.getBuildNumber(activity)
        } catch (e: IllegalStateException) {
            return
        }

        // Finde die Preference (oder PreferenceScreen) anhand des Keys
        val versionPreferenceItem: Preference? = findPreference(VERSION_INFO_KEY)

        versionPreferenceItem?.let { pref ->
            val currentSummary = pref.summary?.toString() ?: "Version {v} (Build {b})"
            val versionInfoText = currentSummary
                .replace(PLACEHOLDER_VERSION, versionName)
                .replace(PLACEHOLDER_BUILD, buildNumber.toString())

            pref.summary = versionInfoText

            if (pref is PreferenceScreen) {
                if (pref.preferenceCount > 0) {
                    val firstChild: Preference? = pref.getPreference(0)
                    firstChild?.title = versionInfoText
                }
            }
        }
    }
}
