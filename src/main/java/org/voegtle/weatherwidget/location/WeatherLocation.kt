package org.voegtle.weatherwidget.location

import android.net.Uri
import org.voegtle.weatherwidget.preferences.LocationPreferences

data class WeatherLocation(val key: LocationIdentifier,
                           var name: String,
                           var shortName: String,
                           val identifier: String,
                           var forecastUrl: Uri,
                           val weatherViewId: Int,
                           val prefShowInWidget: String,
                           val prefShowInApp: String,
                           val prefAlert: String,
                           val prefHighlight: String,
                           val isVisibleInAppByDefault: Boolean = true,
                           val isVisibleInWidgetByDefault: Boolean = true,
                           var preferences: LocationPreferences = LocationPreferences()) {

  val isActive: Boolean
    get() = preferences.showInApp || preferences.showInWidget || preferences.alertActive
}
