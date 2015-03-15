package org.voegtle.weatherwidget.data;

import org.json.JSONException;
import org.json.JSONObject;

public class WeatherJSONObject extends JSONObject {

  @Override
  public JSONObject put(String key, Object value) throws JSONException {
    if (value == null) {
      super.put(key, "");
    } else {
      return super.put(key, value);
    }
    return this;
  }

}
