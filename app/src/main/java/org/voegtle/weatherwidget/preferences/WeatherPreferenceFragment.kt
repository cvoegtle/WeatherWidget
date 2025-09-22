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

    // WICHTIG: Dieser Key MUSS mit dem android:key in deiner res/xml/preferences.xml übereinstimmen,
    // für die PreferenceScreen (oder Preference), die die Versionsinfos anzeigt.
    private val VERSION_INFO_KEY = "version_info_screen_key" // Passe diesen Key ggf. an!

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        var versionName = ""
        var buildNumber = 0

        // Context über requireActivity() holen, da das Fragment zu diesem Zeitpunkt an eine Activity gebunden sein sollte.
        try {
            val activity = requireActivity()
            versionName = ContextUtil.getVersion(activity)
            buildNumber = ContextUtil.getBuildNumber(activity)
        } catch (e: IllegalStateException) {
            // Activity ist nicht verfügbar, kann passieren, wenn das Fragment schnell detached wird.
            // In diesem Fall können die Versionsinfos nicht geladen werden.
            return
        }

        // Finde die Preference (oder PreferenceScreen) anhand des Keys
        val versionPreferenceItem: Preference? = findPreference(VERSION_INFO_KEY)

        versionPreferenceItem?.let { pref ->
            val currentSummary = pref.summary?.toString() ?: "Version {v} (Build {b})" // Fallback, falls Summary nicht in XML gesetzt ist
            val versionInfoText = currentSummary
                .replace(PLACEHOLDER_VERSION, versionName)
                .replace(PLACEHOLDER_BUILD, buildNumber.toString())

            pref.summary = versionInfoText

            // Wenn das gefundene Item eine PreferenceScreen ist und du den Titel des ersten Kindes aktualisieren willst,
            // (entsprechend deiner ursprünglichen Logik):
            if (pref is PreferenceScreen) {
                if (pref.preferenceCount > 0) {
                    val firstChild: Preference? = pref.getPreference(0)
                    firstChild?.title = versionInfoText
                }
            } else {
                // Wenn es eine einfache Preference ist, deren Titel du auch dynamisch setzen wolltest (seltener):
                // pref.title = versionInfoText
            }
        }
    }
    // Die patchPaddingForAndroid15 Methode wurde entfernt.
    // Das Padding sollte durch fitsSystemWindows in der Activity korrekt gehandhabt werden.
}
