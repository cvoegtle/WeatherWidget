package org.voegtle.weatherwidget.state

import org.voegtle.weatherwidget.util.DateUtil

import java.util.Date

data class State(val id: Int, var isExpanded: Boolean = false, var age: Date? = null, var statistics: String = "") {

  fun outdated(): Boolean {
    val oneHourBefore = DateUtil.oneHoureBefore
    return age == null || age!!.before(oneHourBefore)
  }
}
