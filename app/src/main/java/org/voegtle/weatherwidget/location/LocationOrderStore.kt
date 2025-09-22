package org.voegtle.weatherwidget.location

import android.content.Context
import org.voegtle.weatherwidget.preferences.OrderCriteria

class LocationOrderStore(context: Context) {
  private val LOCATION_STORE_PREF_NAME = "LOCATION_STORE" // Name der SharedPreferences-Datei
  private val INDEX_OF_INT_PREFIX = "INDEX_OF_" // Präfix für alte Int-basierte Schlüssel
  private val INDEX_OF_STRING_PREFIX = "INDEX_OF_STRING_" // Neuer Präfix für String-basierte Schlüssel
  private val ORDER_CRITERIA_KEY = "ORDER_CRITERIA"
  private val LATITUDE_KEY = "LATITUDE"
  private val LONGITUDE_KEY = "LONGITUDE"

  companion object {
      const val DEFAULT_INDEX = -1 // Standardwert, wenn kein Index gefunden wird
  }

  private val locationStore = context.getSharedPreferences(LOCATION_STORE_PREF_NAME, Context.MODE_PRIVATE)

  // --- Alte Methoden für Int-basierte View-IDs (bleiben vorerst erhalten) ---
  fun readIndexOf(viewId: Int) = locationStore.getInt(INDEX_OF_INT_PREFIX + viewId, DEFAULT_INDEX) // Verwendet DEFAULT_INDEX

  fun writeIndexOf(viewId: Int, index: Int) {
    val editor = locationStore.edit()
    editor.putInt(INDEX_OF_INT_PREFIX + viewId, index)
    editor.apply()
  }

  // --- Neue Methoden für String-basierte Schlüssel ---
  fun readIndexOfStringKey(key: String): Int {
    return locationStore.getInt(INDEX_OF_STRING_PREFIX + key, DEFAULT_INDEX)
  }

  fun writeIndexOfStringKey(key: String, index: Int) {
    val editor = locationStore.edit()
    editor.putInt(INDEX_OF_STRING_PREFIX + key, index)
    editor.apply()
  }

  // --- Bestehende Methoden für OrderCriteria und Position ---
  fun writeOrderCriteria(orderCriteria: OrderCriteria) {
    val editor = locationStore.edit()
    editor.putString(ORDER_CRITERIA_KEY, orderCriteria.toString())
    editor.apply()
  }

  fun readOrderCriteria(): OrderCriteria {
    val str = locationStore.getString(ORDER_CRITERIA_KEY, OrderCriteria.location.toString())
    return OrderCriteria.byKey(str ?: OrderCriteria.location.toString()) // Sicherstellen, dass str nicht null ist
  }

  fun writePosition(userPosition: Position) {
    val editor = locationStore.edit()
    editor.putFloat(LATITUDE_KEY, userPosition.latitude)
    editor.putFloat(LONGITUDE_KEY, userPosition.longitude)
    editor.apply()
  }

  /**
   * default Position is Paderborn
   */
  fun readPosition(): Position {
    return Position(latitude = locationStore.getFloat(LATITUDE_KEY, 51.7238851F),
        longitude = locationStore.getFloat(LONGITUDE_KEY, 8.7589337F))
  }
}
