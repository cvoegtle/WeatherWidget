package org.voegtle.weatherwidget.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class NotificationSettings(context: Context) {
    private val NOTIFICATION_PREFERENCES = "NOTIFICATION_PREFERENCES"
    private val NOTIFICATION_ENABLED = "ENABLED"
    private val statePreferences: SharedPreferences = context.getSharedPreferences(NOTIFICATION_PREFERENCES, 0)

    fun isEnabled() = statePreferences.getBoolean(NOTIFICATION_ENABLED, true)

    fun saveEnabled(enabled: Boolean) {
        statePreferences.edit {
            putBoolean(NOTIFICATION_ENABLED, enabled)
        }
    }

}