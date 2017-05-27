package org.voegtle.weatherwidget.location

import android.net.Uri
import org.voegtle.weatherwidget.preferences.LocationPreferences

class WeatherLocation(val key: LocationIdentifier) {
  var name: String = ""
  var shortName: String = ""
  var identifier: String? = null
  var forecastUrl: Uri? = null
  var weatherViewId: Int = 0
  var rainIndicatorId: Int = 0
  var prefShowInWidget: String = ""
  var prefShowInApp: String = ""
  var prefAlert: String = ""
  var isVisibleInAppByDefault = true
  var isVisibleInWidgetByDefault = true
  var preferences: LocationPreferences = LocationPreferences()
  var weatherLineId: Int = 0

  val isActive: Boolean
    get() = preferences.showInApp || preferences.showInWidget || preferences.alertActive
}
