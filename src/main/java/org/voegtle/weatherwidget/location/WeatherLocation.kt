package org.voegtle.weatherwidget.location

import android.net.Uri
import org.voegtle.weatherwidget.preferences.LocationPreferences

class WeatherLocation(val key: LocationIdentifier) {
  var name: String? = null
  var shortName: String? = null
  var identifier: String? = null
  var forecastUrl: Uri? = null
  var weatherViewId: Int = 0
  var rainIndicatorId: Int = 0
  var prefShowInWidget: String? = null
  var prefShowInApp: String? = null
  var prefAlert: String? = null
  var isVisibleInAppByDefault = true
  var isVisibleInWidgetByDefault = true
  var preferences: LocationPreferences? = null
  var weatherLineId: Int = 0

  val isActive: Boolean
    get() = preferences!!.isShowInApp || preferences!!.isShowInWidget || preferences!!.isAlertActive
}
