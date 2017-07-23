package org.voegtle.weatherwidget.location

import android.content.Context
import org.voegtle.weatherwidget.preferences.OrderCriteria

class LocationOrderStore(context: Context) {
  private val LOCATION_STORE = "LOCATION_STORE"
  private val INDEX_OF = "INDEX_OF_"
  private val ORDER_CRITERIA = "ORDER_CRITERIA"
  private val ASK_FOR_LOCATION = "ASK_FOR_LOCATION"
  private val LATITUDE = "LATITUDE"
  private val LONGITUDE = "LONGITUDE"

  private val locationStore = context.getSharedPreferences(LOCATION_STORE, Context.MODE_PRIVATE)

  fun readIndexOf(viewId: Int) = locationStore.getInt(INDEX_OF + viewId, 1000)

  fun writeIndexOf(viewId: Int, index: Int) {
    val editor = locationStore.edit()
    editor.putInt(INDEX_OF + viewId, index)
    editor.apply()
  }

  fun writeOrderCriteria(orderCriteria: OrderCriteria) {
    val editor = locationStore.edit()
    editor.putString(ORDER_CRITERIA, orderCriteria.toString())
    editor.putBoolean(ASK_FOR_LOCATION, orderCriteria == OrderCriteria.location)
    editor.apply()
  }

  fun readOrderCriteria(): OrderCriteria {
    val str = locationStore.getString(ORDER_CRITERIA, OrderCriteria.location.toString())
    return OrderCriteria.byKey(str)
  }

  fun askForLocationAccess(): Boolean {
    return locationStore.getBoolean(ASK_FOR_LOCATION, true)
  }

  fun resetAskForLocationAccess() {
    val editor = locationStore.edit()
    editor.putBoolean(ASK_FOR_LOCATION, false)
    editor.apply()
  }

  fun writePosition(userPosition: Position) {
    val editor = locationStore.edit()
    editor.putFloat(LATITUDE, userPosition.latitude)
    editor.putFloat(LONGITUDE, userPosition.longitude)
    editor.apply()
  }

  /**
   * default Position is Paderborn
   */
  fun readPosition(): Position {
    return Position(latitude = locationStore.getFloat(LATITUDE, 51.7238851F),
        longitude = locationStore.getFloat(LONGITUDE, 8.7589337F))
  }
}
