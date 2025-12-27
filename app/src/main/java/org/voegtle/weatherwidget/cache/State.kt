package org.voegtle.weatherwidget.cache

import org.voegtle.weatherwidget.location.LocationIdentifier
import org.voegtle.weatherwidget.util.DateUtil
import java.util.Date

data class State(val id: LocationIdentifier, var isExpanded: Boolean = false, var age: Date? = null, var statistics: String = "") {

  fun outdated(): Boolean {
    val oneHourBefore = DateUtil.oneHouerBefore
    return age == null || age!!.before(oneHourBefore)
  }

  fun makeOutdated() {
    age = null
  }
}
