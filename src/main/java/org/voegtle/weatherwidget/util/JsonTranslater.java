package org.voegtle.weatherwidget.util;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.voegtle.weatherwidget.data.Statistics;
import org.voegtle.weatherwidget.data.StatisticsSet;
import org.voegtle.weatherwidget.data.WeatherJSONObject;

import java.util.HashMap;

public class JsonTranslater {

  private static Float toFloat(JSONObject json, String identifer) throws JSONException {
    if (json.has(identifer)) {
      Object value = json.get(identifer);
      if (value instanceof Number) {
        return ((Number) value).floatValue();
      }
    }
    return null;
  }

  public static HashMap<String, Statistics> toStatistics(String jsonStr) {
    HashMap<String, Statistics> statisticsMap = new HashMap<>();
    try {
      JSONArray jsonStatistics = new JSONArray(jsonStr);
      for (int i = 0; i < jsonStatistics.length(); i++) {
        Statistics stats = toStatistics(jsonStatistics.getJSONObject(i));
        statisticsMap.put(stats.getId(), stats);
      }
    } catch (JSONException ignore) {
    }
    return statisticsMap;
  }

  public static Statistics toSingleStatistics(String jsonStr) {
    try {
      JSONObject json = new JSONObject(jsonStr);
      return toStatistics(json);
    } catch (JSONException ignore) {
    }
    return null;
  }

  private static Statistics toStatistics(JSONObject jsonStatistics) {
    Statistics result = new Statistics();
    try {
      result.setId(jsonStatistics.optString("id"));
      JSONArray jsonStats = jsonStatistics.getJSONArray("stats");
      for (int i = 0; i < jsonStats.length(); i++) {
        JSONObject json = (JSONObject) jsonStats.get(i);
        StatisticsSet statisticsSet = toStatisticsSet(json);
        if (statisticsSet != null) {
          result.add(statisticsSet);
        }

      }
    } catch (JSONException ignore) {
      Log.e("Problem", "Exception parsing server response:", ignore);
    }
    return result;
  }

  private static StatisticsSet toStatisticsSet(JSONObject json) throws JSONException {
    StatisticsSet result = null;

    String rangeStr = json.getString("range");
    Statistics.TimeRange range = Statistics.TimeRange.fromString(rangeStr);
    if (range != null) {
      result = new StatisticsSet(range);

      result.setRain(toFloat(json, "rain"));
      result.setMinTemperature(toFloat(json, "minTemperature"));
      result.setMaxTemperature(toFloat(json, "maxTemperature"));
      result.setKwh(toFloat(json, "kwh"));
    }
    return result;
  }

  public static String toString(Statistics statistics) {
    JSONObject json = new WeatherJSONObject();
    try {
      json.put("id", statistics.getId());

      JSONArray jsonStats = new JSONArray();
      for (StatisticsSet set : statistics.values()) {
        JSONObject jsonObject = toJson(set);
        jsonStats.put(jsonObject);
      }
      json.put("stats", jsonStats);
    } catch (JSONException ignore) {
    }
    return json.toString();
  }

  private static JSONObject toJson(StatisticsSet set) throws JSONException {
    JSONObject json = new WeatherJSONObject();
    json.put("range", set.getRange().toString());
    json.put("rain", set.getRain());
    json.put("minTemperature", set.getMinTemperature());
    json.put("maxTemperature", set.getMaxTemperature());
    json.put("kwh", set.getKwh());
    return json;
  }
}
