package org.voegtle.weatherwidget.state

import android.content.Context
import android.content.SharedPreferences

import java.util.Date

class StateCache(context: Context) {

  private val statePreferences: SharedPreferences

  init {
    statePreferences = context.getSharedPreferences(STATE_CACHE, 0)
  }

  fun read(id: Int): State {
    val state = State(id)
    val age = statePreferences.getLong(getKey(STATE_AGE, id), -1)
    if (age > 0) {
      state.age = Date(age)
    }
    state.isExpanded = statePreferences.getBoolean(getKey(STATE, id), false)
    state.statistics = statePreferences.getString(getKey(STATISTICS, id), "")

    return state
  }

  fun save(state: State) {
    val editor = statePreferences.edit()
    editor.putLong(getKey(STATE_AGE, state.id), state.age!!.time)
    editor.putBoolean(getKey(STATE, state.id), state.isExpanded)
    editor.putString(getKey(STATISTICS, state.id), state.statistics)
    editor.commit()
  }

  private fun getKey(prefix: String, id: Int): String {
    return prefix + id
  }

  companion object {
    private val STATE_CACHE = "STATE"
    private val STATE_AGE = "AGE"
    private val STATE = "STATE"
    private val STATISTICS = "STATISTICS"
  }

}
