package org.voegtle.weatherwidget.data

import org.json.JSONException
import org.json.JSONObject

class WeatherJSONObject : JSONObject() {

  @Throws(JSONException::class)
  override fun put(key: String, value: Any?): JSONObject {
    return super.put(key, value?: "")
  }

}
