package org.voegtle.weatherwidget.preferences

import android.app.AlertDialog
import android.content.DialogInterface
import org.voegtle.weatherwidget.R
import org.voegtle.weatherwidget.WeatherActivity
import org.voegtle.weatherwidget.location.LocationOrderStore

object OrderCriteriaDialogBuilder {

  fun createOrderCriteriaDialog(activity: WeatherActivity): AlertDialog {
    val locationOrderStore = LocationOrderStore(activity.applicationContext)
    val currentOrder = locationOrderStore.readOrderCriteria()

    val builder = AlertDialog.Builder(activity)
    builder.setTitle(R.string.order_criteria)
    builder.setSingleChoiceItems(R.array.orderCriteria, OrderCriteria.index(currentOrder), object : DialogInterface.OnClickListener {
      override fun onClick(dialog: DialogInterface, which: Int) {
        val orderCriteria = OrderCriteria.byIndex(which)
        store(orderCriteria)

        activity.requestPermissions()
        activity.updateWeatherOnce(true)

        dialog.cancel()
      }

      private fun store(orderCriteria: OrderCriteria) {
        locationOrderStore.writeOrderCriteria(orderCriteria)
      }

    })
    return builder.create()
  }

}
