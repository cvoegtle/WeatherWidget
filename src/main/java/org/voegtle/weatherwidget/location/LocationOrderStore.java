package org.voegtle.weatherwidget.location;

import android.content.Context;
import android.content.SharedPreferences;
import org.voegtle.weatherwidget.preferences.OrderCriteria;

public class LocationOrderStore {
  private static final String LOCATION_STORE = "LOCATION_STORE";
  private static final String INDEX_OF = "INDEX_OF_";
  private static final String ORDER_CRITERIA = "ORDER_CRITERIA";

  private SharedPreferences locationStore;

  public LocationOrderStore(Context context) {
    locationStore = context.getSharedPreferences(LOCATION_STORE, Context.MODE_PRIVATE);
  }

  public int readIndexOf(int viewId) {
    return locationStore.getInt(INDEX_OF + viewId, 1000);
  }

  public void writeIndexOf(int viewId, int index) {
    SharedPreferences.Editor editor = locationStore.edit();
    editor.putInt(INDEX_OF + viewId, index);
    editor.commit();
  }

  public void writeOrderCriteria(OrderCriteria orderCriteria) {
    SharedPreferences.Editor editor = locationStore.edit();
    editor.putString(ORDER_CRITERIA, orderCriteria.toString());
    editor.commit();
  }

  public OrderCriteria readOrderCriteria() {
    String str = locationStore.getString(ORDER_CRITERIA, OrderCriteria.location.toString());
    return OrderCriteria.byKey(str);
  }
}
