package org.voegtle.weatherwidget.preferences

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceScreen
import org.voegtle.weatherwidget.R
import org.voegtle.weatherwidget.util.ContextUtil

class WeatherPreferenceFragment : PreferenceFragmentCompat(), PreferenceFragmentCompat.OnPreferenceStartScreenCallback {
    private val PLACEHOLDER_VERSION = "{v}"
    private val PLACEHOLDER_BUILD = "{b}"

    private val VERSION_INFO_KEY = "version"

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        var versionName = ""
        var buildNumber = 0

        try {
            val activity = requireActivity()
            versionName = ContextUtil.getVersion(activity)
            buildNumber = ContextUtil.getBuildNumber(activity)
        } catch (e: IllegalStateException) {
        }

        val versionPreferenceItem: Preference? = findPreference(VERSION_INFO_KEY)

        versionPreferenceItem?.let { pref ->
            val currentSummary = pref.summary?.toString() ?: "Version {v} (Build {b})"
            val versionInfoText = currentSummary
                .replace(PLACEHOLDER_VERSION, versionName)
                .replace(PLACEHOLDER_BUILD, buildNumber.toString())

            pref.summary = versionInfoText

            if (pref is PreferenceScreen && pref.key == VERSION_INFO_KEY) {
                if (pref.preferenceCount > 0) {
                    val firstChild: Preference? = pref.getPreference(0)
                    firstChild?.title = versionInfoText
                }
            }
        }
    }

    override fun onPreferenceStartScreen(
        caller: PreferenceFragmentCompat,
        screen: PreferenceScreen
    ): Boolean {
        val fragment: Fragment = WeatherPreferenceFragment()
        val args = Bundle()
        args.putString(ARG_PREFERENCE_ROOT, screen.key)
        fragment.arguments = args

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(screen.key)
            .commit()
        return true
    }
}
