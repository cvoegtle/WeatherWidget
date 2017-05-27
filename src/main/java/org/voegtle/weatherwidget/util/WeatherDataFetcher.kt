package org.voegtle.weatherwidget.util

import android.util.Log
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.voegtle.weatherwidget.data.Statistics
import org.voegtle.weatherwidget.data.WeatherData
import org.voegtle.weatherwidget.location.LocationIdentifier
import org.voegtle.weatherwidget.location.WeatherLocation

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.ArrayList
import java.util.Date
import java.util.HashMap

class WeatherDataFetcher(private val buildNumber: Int?) {


  fun fetchAllWeatherDataFromServer(locations: List<WeatherLocation>, secret: String): HashMap<LocationIdentifier, WeatherData> {
    val resultList = HashMap<LocationIdentifier, WeatherData>()
    val urlEncodedSecret = StringUtil.urlEncode(secret)
    val locationIdentifiers = concatenateLocations(locations)

    val jsonWeather = getStringFromUrl("https://wettercentral.appspot.com/weatherstation/read?build=" + buildNumber +
        "&locations=" + locationIdentifiers + "&secret=" + urlEncodedSecret)

    if (StringUtil.isNotEmpty(jsonWeather)) {
      try {
        val weatherList = JSONArray(jsonWeather)
        for (i in 0..weatherList.length() - 1) {
          val weather = weatherList.getJSONObject(i)
          val location = getLocation(locations, weather)
          if (location != null) {
            val data = parseWeatherData(location.key, weather)
            parseLocationData(location, weather)
            resultList.put(data.location, data)
          }
        }
      } catch (e: Throwable) {
        Log.e(WeatherDataFetcher::class.java.toString(), "Failed to parse JSON String <$jsonWeather>", e)
      }

    }
    return resultList
  }

  private fun concatenateLocations(locations: List<WeatherLocation>): String {
    val sb = StringBuilder()
    for (location in locations) {
      if (location.isActive) {
        if (sb.length > 0) {
          sb.append(",")
        }
        sb.append(location.identifier)
      }
    }
    return sb.toString()
  }

  fun fetchWeatherDataFromUrl(baseUrl: String): WeatherData? {
    var data: WeatherData? = null
    val jsonWeather = getStringFromUrl(baseUrl + "/weatherstation/query?type=current&new&build=" + buildNumber)

    if (StringUtil.isNotEmpty(jsonWeather)) {
      try {
        val weather = JSONObject(jsonWeather)
        data = getWeatherData(weather)
      } catch (e: Throwable) {
        Log.e(WeatherDataFetcher::class.java.toString(), "Failed to parse JSON String <$jsonWeather>", e)
      }

    }

    return data
  }

  private fun getLocation(locations: List<WeatherLocation>, weather: JSONObject): WeatherLocation? {
    val id = weather.optString("id")
    for (location in locations) {
      if (location.identifier == id) {
        return location
      }
    }
    return null
  }


  @Throws(JSONException::class)
  private fun getWeatherData(weather: JSONObject): WeatherData? {
    val locationIdentifier = LocationIdentifier.getByString(weather.optString("id"))

    if (locationIdentifier == null) {
      return null
    } else {
      return parseWeatherData(locationIdentifier, weather)
    }
  }

  @Throws(JSONException::class)
  private fun parseWeatherData(locationIdentifier: LocationIdentifier, weather: JSONObject): WeatherData {
    val data = WeatherData(locationIdentifier)

    val timestamp = weather.getString("timestamp")
    data.timestamp = Date(timestamp)

    val temperature = weather.get("temperature") as Number
    data.temperature = temperature.toFloat()

    if (weather.has("localtime")) {
      data.localtime = weather.get("localtime") as String
    }

    if (weather.has("inside_temperature")) {
      val insideTemperature = weather.get("inside_temperature") as Number
      data.insideTemperature = insideTemperature.toFloat()
    }

    val humidity = weather.get("humidity") as Number
    data.humidity = humidity.toFloat()

    if (weather.has("inside_humidity")) {
      val insideHumidity = weather.get("inside_humidity") as Number
      data.insideHumidity = insideHumidity.toFloat()
    }

    if (weather.has("watt")) {
      val watt = weather.get("watt") as Number
      data.watt = watt.toFloat()
    }

    if (weather.has("rain")) {
      val rain = weather.get("rain") as Number
      data.rain = rain.toFloat()
    }

    if (weather.has("raining")) {
      data.isRaining = weather.getBoolean("raining")
    }

    if (weather.has("rain_today")) {
      val rainToday = weather.get("rain_today") as Number
      data.rainToday = rainToday.toFloat()
    }

    if (weather.has("wind")) {
      val wind = weather.get("wind") as Number
      data.wind = wind.toFloat()
    }
    return data
  }

  @Throws(JSONException::class)
  private fun parseLocationData(location: WeatherLocation, weather: JSONObject) {
    val locationName = weather.getString("location")
    location.name = locationName

    val locationShort = weather.getString("location_short")
    location.shortName = locationShort
  }

  fun fetchStatisticsFromUrl(locationIds: ArrayList<String>): HashMap<String, Statistics> {
    if (locationIds.size > 0) {
      var concatenatedLocationIds = locationIds[0]
      for (i in 1..locationIds.size - 1) {
        concatenatedLocationIds += "," + locationIds[i]
      }
      val jsonStatistics = getStringFromUrl("https://wettercentral.appspot.com/weatherstation/read?build=" + buildNumber +
          "&locations=" + concatenatedLocationIds + "&type=stats")
      try {
        return JsonTranslator.toStatistics(jsonStatistics)
      } catch (e: Throwable) {
        Log.e(WeatherDataFetcher::class.java.toString(), "Failed to parse JSON String <$jsonStatistics>", e)
      }

    }
    return HashMap()
  }

  private val COMMUNICATION_TIMEOUT = 60000

  private fun getStringFromUrl(uri: String): String {
    var response = ""
    try {
      val url = URL(uri)
      val connection = url.openConnection() as HttpURLConnection
      try {
        connection.connectTimeout = COMMUNICATION_TIMEOUT
        connection.readTimeout = COMMUNICATION_TIMEOUT
        response = readStream(connection.inputStream)
      } finally {
        connection.disconnect()
      }
    } catch (e: Throwable) {
      Log.d(WeatherDataFetcher::class.java.toString(), "Failed to download weather data", e)
    }

    return response
  }

  @Throws(IOException::class)
  private fun readStream(content: InputStream): String {
    val builder = StringBuilder()
    val reader = BufferedReader(InputStreamReader(content, "UTF-8"))
    var line: String? = reader.readLine()
    while (line != null) {
      builder.append(line)
      line = reader.readLine()
    }
    reader.close()
    return builder.toString()
  }


}
