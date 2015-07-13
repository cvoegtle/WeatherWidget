package org.voegtle.weatherwidget.preferences;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import org.voegtle.weatherwidget.R;
import org.voegtle.weatherwidget.location.LocationOrderStore;
import org.voegtle.weatherwidget.util.WeatherDataUpdater;

public class OrderCriteriaDialogBuilder {

  public static AlertDialog createOrderCriteriaDialog(final Activity activity, final WeatherDataUpdater updater) {
    final LocationOrderStore locationOrderStore = new LocationOrderStore(activity.getApplicationContext());
    OrderCriteria currentOrder = locationOrderStore.readOrderCriteria();

    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
    builder.setTitle(R.string.order_criteria);
    builder.setSingleChoiceItems(R.array.orderCriteria, OrderCriteria.index(currentOrder), new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        OrderCriteria orderCriteria = OrderCriteria.byIndex(which);
        store(orderCriteria);
        updater.updateWeatherOnce(true);
        dialog.cancel();
      }

      private void store(OrderCriteria orderCriteria) {
        locationOrderStore.writeOrderCriteria(orderCriteria);
      }

    });
    return builder.create();
  }

}
