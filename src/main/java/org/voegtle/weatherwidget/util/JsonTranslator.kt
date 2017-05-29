package org.voegtle.weatherwidget.util

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.voegtle.weatherwidget.data.Statistics
import org.voegtle.weatherwidget.data.StatisticsSet
import org.voegtle.weatherwidget.data.WeatherJSONObject
import java.util.*

object JsonTranslator {

  @Throws(JSONException::class)
  private fun toFloat(json: JSONObject, identifier: String): Float? {
    if (json.has(identifier)) {
      val value = json.get(identifier)
      if (value is Number) {
        return value.toFloat()
      }
    }
    return null
  }

  internal fun toStatistics(jsonStr: String): HashMap<String, Statistics> {
    val statisticsMap = HashMap<String, Statistics>()
    try {
      val jsonStatistics = JSONArray(jsonStr)
      for (i in 0..jsonStatistics.length() - 1) {
        val stats = toStatistics(jsonStatistics.getJSONObject(i))
        if (stats != null) {
          statisticsMap.put(stats.id, stats)
        }
      }
    } catch (ignore: JSONException) {
    }

    return statisticsMap
  }

  internal fun toSingleStatistics(jsonStr: String): Statistics {
    val json = JSONObject(jsonStr)
    return toStatistics(json)
  }

  private fun toStatistics(jsonStatistics: JSONObject): Statistics {
    val result = Statistics(jsonStatistics.optString("id"))
    val jsonStats = jsonStatistics.getJSONArray("stats")
    for (i in 0..jsonStats.length() - 1) {
      val statisticsSet = toStatisticsSet(jsonStats.get(i) as JSONObject)
      if (statisticsSet != null) {
        result.add(statisticsSet)
      }
    }
    return result
  }

  private fun toStatisticsSet(json: JSONObject): StatisticsSet? {
    var result: StatisticsSet? = null

    val rangeStr = json.getString("range")
    val range = Statistics.TimeRange.fromString(rangeStr)
    if (range != null) {
      result = StatisticsSet(range = range, rain = toFloat(json, "rain"),
          minTemperature = toFloat(json, "minTemperature"), maxTemperature = toFloat(json, "maxTemperature"),
          kwh = toFloat(json, "kwh"))
    }
    return result
  }

  fun toString(statistics: Statistics): String {
    val json = WeatherJSONObject()
    try {
      json.put("id", statistics.id)

      val jsonStats = JSONArray()
      for (set in statistics.values()) {
        val jsonObject = toJson(set)
        jsonStats.put(jsonObject)
      }
      json.put("stats", jsonStats)
    } catch (ignore: JSONException) {
    }

    return json.toString()
  }

  @Throws(JSONException::class)
  private fun toJson(set: StatisticsSet): JSONObject {
    val json = WeatherJSONObject()
    json.put("range", set.range.toString())
    json.put("rain", set.rain)
    json.put("minTemperature", set.minTemperature)
    json.put("maxTemperature", set.maxTemperature)
    json.put("kwh", set.kwh)
    return json
  }
}
