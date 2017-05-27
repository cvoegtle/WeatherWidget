package org.voegtle.weatherwidget.preferences

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import org.voegtle.weatherwidget.R
import org.voegtle.weatherwidget.location.LocationOrderStore
import org.voegtle.weatherwidget.util.WeatherDataUpdater

object OrderCriteriaDialogBuilder {

  fun createOrderCriteriaDialog(activity: Activity, updater: WeatherDataUpdater): AlertDialog {
    val locationOrderStore = LocationOrderStore(activity.applicationContext)
    val currentOrder = locationOrderStore.readOrderCriteria()

    val builder = AlertDialog.Builder(activity)
    builder.setTitle(R.string.order_criteria)
    builder.setSingleChoiceItems(R.array.orderCriteria, OrderCriteria.index(currentOrder), object : DialogInterface.OnClickListener {
      override fun onClick(dialog: DialogInterface, which: Int) {
        val orderCriteria = OrderCriteria.byIndex(which)
        store(orderCriteria)
        updater.updateWeatherOnce(true)
        dialog.cancel()
      }

      private fun store(orderCriteria: OrderCriteria) {
        locationOrderStore.writeOrderCriteria(orderCriteria)
      }

    })
    return builder.create()
  }

}
