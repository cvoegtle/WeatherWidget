package org.voegtle.weatherwidget.location

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import org.voegtle.weatherwidget.preferences.OrderCriteria
import androidx.core.content.edit

class LocationOrderStore(context: Context) {
  private val LOCATION_STORE = "LOCATION_STORE"
  private val ORDER_CRITERIA = "ORDER_CRITERIA"
  private val LATITUDE = "LATITUDE"
  private val LONGITUDE = "LONGITUDE"

  private val locationStore = context.getSharedPreferences(LOCATION_STORE, Context.MODE_PRIVATE)

  fun writeOrderCriteria(orderCriteria: OrderCriteria) {
      locationStore.edit {
          putString(ORDER_CRITERIA, orderCriteria.toString())
      }
  }

  fun readOrderCriteria(): OrderCriteria {
    val str = locationStore.getString(ORDER_CRITERIA, OrderCriteria.location.toString())
    return OrderCriteria.byKey(str!!)
  }

  fun writePosition(userPosition: Position) {
      locationStore.edit {
          putFloat(LATITUDE, userPosition.latitude)
          putFloat(LONGITUDE, userPosition.longitude)
      }
  }

  /**
   * default Position is Paderborn
   */
  fun readPosition(): Position {
    return Position(latitude = locationStore.getFloat(LATITUDE, 51.7238851F),
        longitude = locationStore.getFloat(LONGITUDE, 8.7589337F))
  }
}
