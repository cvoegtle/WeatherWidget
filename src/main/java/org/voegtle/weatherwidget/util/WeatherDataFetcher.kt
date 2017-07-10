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
          getLocation(locations, weather)?.let {
            val data = parseWeatherData(it.key, weather)
            parseLocationData(it, weather)
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
    locations.forEach { location ->
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
    val jsonWeather = getStringFromUrl(baseUrl + "/weatherstation/query?type=current&new&build=" + buildNumber)

    if (StringUtil.isNotEmpty(jsonWeather)) {
      try {
        val weather = JSONObject(jsonWeather)
        return getWeatherData(weather)
      } catch (e: Throwable) {
        Log.e(WeatherDataFetcher::class.java.toString(), "Failed to parse JSON String <$jsonWeather>", e)
      }
    }
    return null
  }

  private fun getLocation(locations: List<WeatherLocation>, weather: JSONObject): WeatherLocation? {
    val id = weather.optString("id")
    return locations.firstOrNull { it.identifier == id }
  }


  @Throws(JSONException::class)
  private fun getWeatherData(weather: JSONObject): WeatherData? {
    val locationIdentifier = LocationIdentifier.getByString(weather.optString("id"))

    return if (locationIdentifier == null) null else parseWeatherData(locationIdentifier, weather)
  }

  @Throws(JSONException::class)
  private fun parseWeatherData(locationIdentifier: LocationIdentifier, weather: JSONObject): WeatherData {

    val timestamp = weather.getString("timestamp")
    val temperature = weather.get("temperature") as Number
    val humidity = weather.get("humidity") as Number
    return WeatherData(location = locationIdentifier,
        timestamp = Date(timestamp),
        temperature = temperature.toFloat(),
        humidity = humidity.toFloat(),
        localtime = if (weather.has("localtime")) weather.get("localtime") as String else "",
        insideTemperature = getOptionalNumber(weather, "inside_temperature"),
        insideHumidity = getOptionalNumber(weather, "inside_humidity"),
        watt = getOptionalNumber(weather, "watt"),
        rain = getOptionalNumber(weather, "rain"),
        rainToday = getOptionalNumber(weather, "rain_today"),
        wind = getOptionalNumber(weather, "wind"),
        isRaining = weather.has("raining") && weather.getBoolean("raining"))
  }

  private fun getOptionalNumber(json: JSONObject, name: String): Float? {
    return if (json.has(name)) (json.get(name) as Number).toFloat() else null
  }

  @Throws(JSONException::class)
  private fun parseLocationData(location: WeatherLocation, weather: JSONObject) {
    location.name = weather.getString("location")
    location.shortName = weather.getString("location_short")
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
