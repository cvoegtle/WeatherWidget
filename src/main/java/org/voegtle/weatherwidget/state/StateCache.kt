package org.voegtle.weatherwidget.state

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.annotation.RequiresApi
import java.util.*

class StateCache(context: Context) {
  private val STATE_CACHE = "STATE"
  private val STATE_AGE = "AGE"
  private val STATE = "STATE"
  private val STATISTICS = "STATISTICS"

  private val statePreferences: SharedPreferences = context.getSharedPreferences(STATE_CACHE, 0)

  fun read(id: Int): State = State(id = id,
      age = readAge(id),
      isExpanded = statePreferences.getBoolean(getKey(STATE, id), false),
      statistics = statePreferences.getString(getKey(STATISTICS, id), "")!!)

  fun readAge(id: Int): Date? {
    val age = statePreferences.getLong(getKey(STATE_AGE, id), -1)
    return if (age > 0) Date(age) else null
  }

  @RequiresApi(Build.VERSION_CODES.GINGERBREAD) fun save(state: State) {
    val editor = statePreferences.edit()
    editor.putLong(getKey(STATE_AGE, state.id), state.age?.time ?: -1)
    editor.putBoolean(getKey(STATE, state.id), state.isExpanded)
    editor.putString(getKey(STATISTICS, state.id), state.statistics)
    editor.apply()
  }

  private fun getKey(prefix: String, id: Int) = prefix + id

}
