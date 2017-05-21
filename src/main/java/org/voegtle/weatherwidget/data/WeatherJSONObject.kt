package org.voegtle.weatherwidget.data

import org.json.JSONException
import org.json.JSONObject

class WeatherJSONObject : JSONObject() {

    @Throws(JSONException::class)
    override fun put(key: String, value: Any?): JSONObject {
        if (value == null) {
            super.put(key, "")
        } else {
            return super.put(key, value)
        }
        return this
    }

}
