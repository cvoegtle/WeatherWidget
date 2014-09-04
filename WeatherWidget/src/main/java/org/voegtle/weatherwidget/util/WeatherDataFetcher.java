package org.voegtle.weatherwidget.util;

import android.net.Uri;
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
import org.voegtle.weatherwidget.data.RainData;
import org.voegtle.weatherwidget.data.WeatherData;
import org.voegtle.weatherwidget.location.LocationIdentifier;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.HashMap;

public class WeatherDataFetcher {

  public WeatherDataFetcher() {
  }

  @SuppressWarnings("deprecation")
  public HashMap<LocationIdentifier, WeatherData> fetchAllWeatherDataFromServer() {
    HashMap<LocationIdentifier, WeatherData> resultList = new HashMap<LocationIdentifier, WeatherData>();

    String jsonWeather = getStringFromUrl("http://tegelwetter.appspot.com/weatherstation/query?type=all");

    if (StringUtil.isNotEmpty(jsonWeather)) {
      try {
        JSONArray weatherList = new JSONArray(jsonWeather);
        for (int i = 0; i < weatherList.length(); i++) {
          JSONObject weather = weatherList.getJSONObject(i);
          WeatherData data = getWeatherData(weather);
          if (data != null) {
            resultList.put(data.getLocation(), data);
          }
        }
      } catch (Throwable e) {
        Log.e(WeatherDataFetcher.class.toString(), "Failed to parse JSON String <" + jsonWeather + ">", e);
      }
    }
    return resultList;
  }

  @SuppressWarnings("deprecation")
  public WeatherData fetchWeatherDataFromUrl(String baseUrl) {
    WeatherData data = null;
    String jsonWeather = getStringFromUrl(baseUrl + "/weatherstation/query?type=current");

    if (StringUtil.isNotEmpty(jsonWeather)) {
      try {
        JSONObject weather = new JSONObject(jsonWeather);
        data = getWeatherData(weather);
      } catch (Throwable e) {
        Log.e(WeatherDataFetcher.class.toString(), "Failed to parse JSON String <" + jsonWeather + ">", e);
      }
    }

    return data;
  }

  private WeatherData getWeatherData(JSONObject weather) throws JSONException {
    LocationIdentifier locationIdentifier = LocationIdentifier.getByString(weather.optString("location"));

    if (locationIdentifier == null) {
      return null;
    } else {
      return parseWeatherData(locationIdentifier, weather);
    }
  }

  private WeatherData parseWeatherData(LocationIdentifier locationIdentifier, JSONObject weather) throws JSONException {
    WeatherData data = new WeatherData(locationIdentifier);

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

  public RainData fetchRainDataFromUrl(Uri uri) {
    String jsonWeather = getStringFromUrl(uri.toString());
    try {
      JSONObject rain = new JSONObject(jsonWeather);
      return getRainData(rain);
    } catch (Throwable e) {
      Log.e(WeatherDataFetcher.class.toString(), "Failed to parse JSON String <" + jsonWeather + ">", e);
    }
    return new RainData();
  }

  private RainData getRainData(JSONObject rain) throws JSONException {
    RainData rainData = new RainData();

    Object today = rain.get("today");
    if (today instanceof Number) {
      rainData.setRainToday(((Number) today).floatValue());
    }

    Object lastHour = rain.get("lastHour");
    if (lastHour instanceof Number) {
      rainData.setRainLastHour(((Number) lastHour).floatValue());
    }

    Object yesterday = rain.get("yesterday");
    if (yesterday instanceof Number) {
      rainData.setRainYeasterday(((Number) yesterday).floatValue());
    }

    Object lastWeek = rain.get("lastWeek");
    if (lastWeek instanceof Number) {
      rainData.setRainLastWeek(((Number) lastWeek).floatValue());
    }

    Object last30days = rain.get("last30days");
    if (last30days instanceof Number) {
      rainData.setRain30Days(((Number) last30days).floatValue());
    }

    return rainData;
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
        Log.e(WeatherDataFetcher.class.toString(), "Failed to download weather data with statuscode=" + statusCode);
      }
    } catch (Exception e) {
      Log.d(WeatherDataFetcher.class.toString(), "Failed to download weather data", e);
    }
    return builder.toString();
  }


}
