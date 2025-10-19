package org.voegtle.weatherwidget.location

import android.net.Uri
import org.voegtle.weatherwidget.data.WeatherData
import org.voegtle.weatherwidget.preferences.LocationPreferences
import androidx.core.net.toUri

data class WeatherLocation(val key: LocationIdentifier,
                           var name: String,
                           var shortName: String,
                           val identifier: String,
                           @Transient var forecastUrl: Uri,
                           val prefShowInWidget: String,
                           val prefShowInApp: String,
                           val prefFavorite: String,
                           val isVisibleInAppByDefault: Boolean = true,
                           val isVisibleInWidgetByDefault: Boolean = true,
                           var preferences: LocationPreferences = LocationPreferences()) {

  val isActive: Boolean
    get() = preferences.showInApp || preferences.showInWidget
}
