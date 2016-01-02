package org.voegtle.weatherwidget.util;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.voegtle.weatherwidget.data.Statistics;
import org.voegtle.weatherwidget.data.WeatherData;
import org.voegtle.weatherwidget.location.LocationIdentifier;
import org.voegtle.weatherwidget.location.WeatherLocation;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class WeatherDataFetcher {

  public WeatherDataFetcher() {
  }


  @SuppressWarnings("deprecation")
  public HashMap<LocationIdentifier, WeatherData> fetchAllWeatherDataFromServer(List<WeatherLocation> locations, String secret) {
    HashMap<LocationIdentifier, WeatherData> resultList = new HashMap<>();
    String urlEncodedSecret = StringUtil.urlEncode(secret);
    String locationIdentifiers = concatenateLocations(locations);

    String jsonWeather = getStringFromUrl("https://wettercentral.appspot.com/weatherstation/read?utf8&locations=" + locationIdentifiers + "&secret=" + urlEncodedSecret);

    if (StringUtil.isNotEmpty(jsonWeather)) {
      try {
        JSONArray weatherList = new JSONArray(jsonWeather);
        for (int i = 0; i < weatherList.length(); i++) {
          JSONObject weather = weatherList.getJSONObject(i);
          WeatherLocation location = getLocation(locations, weather);
          if (location != null) {
            WeatherData data = parseWeatherData(location.getKey(), weather);
            parseLocationData(location, weather);
            resultList.put(data.getLocation(), data);
          }
        }
      } catch (Throwable e) {
        Log.e(WeatherDataFetcher.class.toString(), "Failed to parse JSON String <" + jsonWeather + ">", e);
      }
    }
    return resultList;
  }

  private String concatenateLocations(List<WeatherLocation> locations) {
    StringBuilder sb = new StringBuilder();
    for (WeatherLocation location : locations) {
      if (location.isActive()) {
        if (sb.length() > 0) {
          sb.append(",");
        }
        sb.append(location.getIdentifier());
      }
    }
    return sb.toString();
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

  private WeatherLocation getLocation(List<WeatherLocation> locations, JSONObject weather) {
    String id = weather.optString("id");
    for (WeatherLocation location : locations) {
      if (location.getIdentifier().equals(id)) {
        return location;
      }
    }
    return null;
  }


  private WeatherData getWeatherData(JSONObject weather) throws JSONException {
    LocationIdentifier locationIdentifier = LocationIdentifier.getByString(weather.optString("id"));

    if (locationIdentifier == null) {
      return null;
    } else {
      return parseWeatherData(locationIdentifier, weather);
    }
  }

  @SuppressWarnings("deprecation")
  private WeatherData parseWeatherData(LocationIdentifier locationIdentifier, JSONObject weather) throws JSONException {
    WeatherData data = new WeatherData(locationIdentifier);

    String timestamp = weather.getString("timestamp");
    data.setTimestamp(new Date(timestamp));

    Number temperature = (Number) weather.get("temperature");
    data.setTemperature(temperature.floatValue());

    if (weather.has("inside_temperature")) {
      Number insideTemperature = (Number) weather.get("inside_temperature");
      data.setInsideTemperature(insideTemperature.floatValue());
    }

    Number humidity = (Number) weather.get("humidity");
    data.setHumidity(humidity.floatValue());

    if (weather.has("inside_humidity")) {
      Number insideHumidity = (Number) weather.get("inside_humidity");
      data.setInsideHumidity(insideHumidity.floatValue());
    }

    if (weather.has("watt")) {
      Number watt = (Number) weather.get("watt");
      data.setWatt(watt.floatValue());
    }

    Object rain = weather.get("rain");
    if (rain instanceof Number) {
      data.setRain(((Number) rain).floatValue());
    }

    Object rainToday = weather.get("rain_today");
    if (rainToday instanceof Number) {
      data.setRainToday(((Number) rainToday).floatValue());
    }

    Object wind = weather.get("wind");
    if (wind instanceof Number) {
      data.setWind(((Number)wind).floatValue());
    }
    return data;
  }

  private void parseLocationData(WeatherLocation location, JSONObject weather) throws JSONException {
    String locationName = weather.getString("location");
    location.setName(locationName);

    String locationShort = weather.getString("location_short");
    location.setShortName(locationShort);
  }

  public HashMap<String, Statistics> fetchStatisticsFromUrl(ArrayList<String> locationIds) {
    if (locationIds.size() > 0) {
      String concatenatedLocationIds = locationIds.get(0);
      for (int i = 1; i < locationIds.size(); i++) {
        concatenatedLocationIds += "," + locationIds.get(i);
      }
      String jsonStatistics = getStringFromUrl("https://wettercentral.appspot.com/weatherstation/read?utf8&locations=" + concatenatedLocationIds + "&type=stats");
      try {
        return JsonTranslater.toStatistics(jsonStatistics);
      } catch (Throwable e) {
        Log.e(WeatherDataFetcher.class.toString(), "Failed to parse JSON String <" + jsonStatistics + ">", e);
      }
    }
    return new HashMap<>();
  }

  private String getStringFromUrl(String uri) {
    StringBuilder builder = new StringBuilder();
    try {
      URL url = new URL(uri);
      URLConnection connection = url.openConnection();
      InputStream content = connection.getInputStream();
      BufferedReader reader = new BufferedReader(new InputStreamReader(content, "UTF-8"));
      String line;
      while ((line = reader.readLine()) != null) {
        builder.append(line);
      }
      reader.close();
    } catch (Throwable e) {
      Log.d(WeatherDataFetcher.class.toString(), "Failed to download weather data", e);
    }
    return builder.toString();
  }


}
