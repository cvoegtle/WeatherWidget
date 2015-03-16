package org.voegtle.weatherwidget.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.voegtle.weatherwidget.data.Statistics;
import org.voegtle.weatherwidget.data.StatisticsSet;
import org.voegtle.weatherwidget.data.WeatherJSONObject;

public class JsonTranslater {

  private static Float toFloat(JSONObject json, String identifer) throws JSONException {
    Object value = json.get(identifer);
    if (value instanceof Number) {
      return ((Number) value).floatValue();
    }
    return null;
  }

  public static Statistics toStatistics(String jsonStr) {
    Statistics result = new Statistics();
    try {
      JSONArray jsonArray = new JSONArray(jsonStr);
      for (int i = 0; i < jsonArray.length(); i++) {
        JSONObject json = (JSONObject) jsonArray.get(i);
        StatisticsSet statisticsSet = toStatisticsSet(json);
        if (statisticsSet != null) {
          result.add(statisticsSet);
        }

      }
    } catch (JSONException ignore) {
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
    }
    return result;
  }

  public static String toString(Statistics statistics) {
    JSONArray json = new JSONArray();
    for (StatisticsSet set : statistics.values()) {
      try {
        JSONObject jsonObject = toJson(set);
        json.put(jsonObject);
      } catch (JSONException ignore) {
      }
    }

    return json.toString();
  }

  private static JSONObject toJson(StatisticsSet set) throws JSONException {
    JSONObject json = new WeatherJSONObject();
    json.put("range", set.getRange().toString());
    json.put("rain", set.getRain());
    json.put("minTemperature", set.getMinTemperature());
    json.put("maxTemperature", set.getMaxTemperature());
    return json;
  }
}
