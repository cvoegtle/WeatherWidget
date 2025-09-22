package org.voegtle.weatherwidget.state

import android.content.Context
import android.content.SharedPreferences
import java.util.Date

class StateCache(context: Context) {
  private val STATE_CACHE_PREF_NAME = "STATE_CACHE"
  private val STATE_AGE_PREFIX = "AGE_"
  // private val STATE_EXPANDED_PREFIX = "EXPANDED_" // Nicht mehr benötigt
  private val STATISTICS_PREFIX = "STATISTICS_"

  private val statePreferences: SharedPreferences = context.getSharedPreferences(STATE_CACHE_PREF_NAME, 0)

  fun read(id: String): State = State(
      id = id,
      age = readAge(id),
      // isExpanded = statePreferences.getBoolean(getKey(STATE_EXPANDED_PREFIX, id), false), // Entfernt
      statistics = statePreferences.getString(getKey(STATISTICS_PREFIX, id), "") ?: ""
  )

  fun readAge(id: String): Date? {
    val ageTimestamp = statePreferences.getLong(getKey(STATE_AGE_PREFIX, id), -1)
    return if (ageTimestamp > 0) Date(ageTimestamp) else null
  }

  fun save(state: State) {
    val editor = statePreferences.edit()
    editor.putLong(getKey(STATE_AGE_PREFIX, state.id), state.age?.time ?: -1)
    // editor.putBoolean(getKey(STATE_EXPANDED_PREFIX, state.id), state.isExpanded) // Entfernt
    editor.putString(getKey(STATISTICS_PREFIX, state.id), state.statistics)
    editor.apply()
  }

  private fun getKey(prefix: String, id: String) = prefix + id
}
