package org.voegtle.weatherwidget.location;

import android.content.Context;
import android.content.SharedPreferences;

public class LocationOrderStore {
  private static final String LOCATION_STORE = "LOCATION_STORE";
  private static final String INDEX_OF = "INDEX_OF_";

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
}
