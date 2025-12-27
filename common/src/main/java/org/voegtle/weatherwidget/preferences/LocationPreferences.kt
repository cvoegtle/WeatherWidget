package org.voegtle.weatherwidget.preferences

data class LocationPreferences(val showInWidget: Boolean = false,
                               val showInApp: Boolean = false,
                               val favorite: Boolean = false)