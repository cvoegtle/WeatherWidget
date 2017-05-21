package org.voegtle.weatherwidget.location

import android.content.Context
import android.content.SharedPreferences
import org.voegtle.weatherwidget.preferences.OrderCriteria

class LocationOrderStore(context: Context) {

  private val locationStore: SharedPreferences

  init {
    locationStore = context.getSharedPreferences(LOCATION_STORE, Context.MODE_PRIVATE)
  }

  fun readIndexOf(viewId: Int): Int {
    return locationStore.getInt(INDEX_OF + viewId, 1000)
  }

  fun writeIndexOf(viewId: Int, index: Int) {
    val editor = locationStore.edit()
    editor.putInt(INDEX_OF + viewId, index)
    editor.commit()
  }

  fun writeOrderCriteria(orderCriteria: OrderCriteria) {
    val editor = locationStore.edit()
    editor.putString(ORDER_CRITERIA, orderCriteria.toString())
    editor.commit()
  }

  fun readOrderCriteria(): OrderCriteria {
    val str = locationStore.getString(ORDER_CRITERIA, OrderCriteria.location.toString())
    return OrderCriteria.byKey(str)
  }

  companion object {
    private val LOCATION_STORE = "LOCATION_STORE"
    private val INDEX_OF = "INDEX_OF_"
    private val ORDER_CRITERIA = "ORDER_CRITERIA"
  }
}
