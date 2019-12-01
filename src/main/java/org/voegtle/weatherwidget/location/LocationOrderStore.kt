package org.voegtle.weatherwidget.location

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import org.voegtle.weatherwidget.preferences.OrderCriteria

class LocationOrderStore(context: Context) {
  private val LOCATION_STORE = "LOCATION_STORE"
  private val INDEX_OF = "INDEX_OF_"
  private val ORDER_CRITERIA = "ORDER_CRITERIA"
  private val LATITUDE = "LATITUDE"
  private val LONGITUDE = "LONGITUDE"

  private val locationStore = context.getSharedPreferences(LOCATION_STORE, Context.MODE_PRIVATE)

  fun readIndexOf(viewId: Int) = locationStore.getInt(INDEX_OF + viewId, 1000)

  @RequiresApi(Build.VERSION_CODES.GINGERBREAD) fun writeIndexOf(viewId: Int, index: Int) {
    val editor = locationStore.edit()
    editor.putInt(INDEX_OF + viewId, index)
    editor.apply()
  }

  @RequiresApi(Build.VERSION_CODES.GINGERBREAD) fun writeOrderCriteria(orderCriteria: OrderCriteria) {
    val editor = locationStore.edit()
    editor.putString(ORDER_CRITERIA, orderCriteria.toString())
    editor.apply()
  }

  fun readOrderCriteria(): OrderCriteria {
    val str = locationStore.getString(ORDER_CRITERIA, OrderCriteria.location.toString())
    return OrderCriteria.byKey(str!!)
  }

  @RequiresApi(Build.VERSION_CODES.GINGERBREAD) fun writePosition(userPosition: Position) {
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
