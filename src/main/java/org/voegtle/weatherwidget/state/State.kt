package org.voegtle.weatherwidget.state

import org.voegtle.weatherwidget.util.DateUtil

import java.util.Date

data class State(var id: Int, var isExpanded: Boolean = false, var age: Date? = null, var statistics: String? = null) {

  fun outdated(): Boolean {
    val oneHourBefore = DateUtil.getOneHoureBefore()
    return age == null || age!!.before(oneHourBefore)
  }
}
