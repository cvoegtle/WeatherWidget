package org.voegtle.weatherwidget.util;

import android.util.Log;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.voegtle.weatherwidget.data.WeatherData;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class WeatherDataFetcher {

  public WeatherDataFetcher() {
  }

  @SuppressWarnings("deprecation")
  public HashMap<String, WeatherData> fetchAllWeatherDataFromServer() {
    HashMap<String, WeatherData> resultList = new HashMap<String, WeatherData>();
    String jsonWeather = getStringFromUrl("http://tegelwetter.appspot.com/weatherstation/query?type=all");
    try {
      JSONArray weatherList = new JSONArray(jsonWeather);
      for (int i = 0; i < weatherList.length(); i++) {
        JSONObject weather = weatherList.getJSONObject(i);
        WeatherData data = getWeatherData(weather);

        resultList.put(data.getLocation(), data);
      }
    } catch (Throwable e) {
      Log.e(WeatherDataFetcher.class.toString(), "Failed to parse JSON String <" + jsonWeather + ">", e);
    }
    return resultList;
  }

  @SuppressWarnings("deprecation")
  public WeatherData fetchWeatherDataFromUrl(String baseUrl) {
    WeatherData data = null;
    String jsonWeather = getStringFromUrl(baseUrl + "/weatherstation/query?type=current");
    try {
      JSONObject weather = new JSONObject(jsonWeather);
      data = getWeatherData(weather);
    } catch (Throwable e) {
      Log.e(WeatherDataFetcher.class.toString(), "Failed to parse JSON String <" + jsonWeather + ">", e);
    }

    return data;
  }

  private WeatherData getWeatherData(JSONObject weather) throws JSONException {
    WeatherData data = new WeatherData();

    data.setLocation(weather.optString("location"));

    String timestamp = weather.getString("timestamp");
    data.setTimestamp(new Date(timestamp));

    Number temperature = (Number) weather.get("temperature");
    data.setTemperature(temperature.floatValue());

    Number humidity = (Number) weather.get("humidity");
    data.setHumidity(humidity.floatValue());

    Object rain = weather.get("rain");
    if (rain instanceof Number) {
      data.setRain(((Number) rain).floatValue());
    }

    Object rainToday = weather.get("rain_today");
    if (rainToday instanceof Number) {
      data.setRainToday(((Number) rainToday).floatValue());
    }
    return data;
  }

  private String getStringFromUrl(String uri) {
    StringBuilder builder = new StringBuilder();
    HttpClient client = new DefaultHttpClient();
    HttpGet httpGet = new HttpGet(uri);
    try {
      HttpResponse response = client.execute(httpGet);
      StatusLine statusLine = response.getStatusLine();
      int statusCode = statusLine.getStatusCode();
      if (statusCode == 200) {
        HttpEntity entity = response.getEntity();
        InputStream content = entity.getContent();
        BufferedReader reader = new BufferedReader(new InputStreamReader(content));
        String line;
        while ((line = reader.readLine()) != null) {
          builder.append(line);
        }
      } else {
        Log.e(WeatherDataFetcher.class.toString(), "Failed to download file");
      }
    } catch (Exception e) {
      Log.d(WeatherDataFetcher.class.toString(), "Failed to download file", e);
    }
    return builder.toString();
  }


}
