package org.voegtle.weatherwidget.util

import android.net.Uri
import android.util.Log
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.voegtle.weatherwidget.data.Statistics
import org.voegtle.weatherwidget.data.WeatherData
import org.voegtle.weatherwidget.location.LocationIdentifier
import org.voegtle.weatherwidget.location.Position
import org.voegtle.weatherwidget.location.WeatherLocation
import java.util.Date

data class FetchAllResponse(val valid: Boolean,
                            val weatherMap: HashMap<LocationIdentifier, WeatherData> = HashMap())

class WeatherDataFetcher(private val buildNumber: Int?) {
  fun fetchAllWeatherDataFromServer(locations: List<WeatherLocation>,
                                    secret: String): FetchAllResponse {
    val urlEncodedSecret = StringUtil.urlEncode(secret)
    val locationIdentifiers = concatenateLocations(locations)

    val jsonWeather = RawDataFetcher.getStringFromUrl(
        "https://wettercentral.appspot.com/weatherstation/read?build=" + buildNumber +
            "&locations=" + locationIdentifiers + "&secret=" + urlEncodedSecret)

    if (jsonWeather.valid) {
      try {
        val resultList = HashMap<LocationIdentifier, WeatherData>()
        val weatherList = JSONArray(jsonWeather.data)
        (0 until weatherList.length()).map { weatherList.getJSONObject(it) }.forEach { weather ->
          getLocation(locations, weather)?.let {
            val data = parseWeatherData(it.key, weather)
            parseLocationData(it, weather)
            resultList.put(data.location, data)
          }
        }
        return FetchAllResponse(true, resultList)
      } catch (e: Throwable) {
        Log.e(WeatherDataFetcher::class.java.toString(), "Failed to parse JSON String <$jsonWeather>", e)
      }
    }
    return FetchAllResponse(false)
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
    val jsonWeather = RawDataFetcher.getStringFromUrl(
        baseUrl + "/weatherstation/query?type=current&new&build=" + buildNumber)

    if (jsonWeather.valid) {
      try {
        val weather = JSONObject(jsonWeather.data)
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
    val position = Position(latitude = getOptionalNumber(weather, "latitude") ?: 0.0F,
                            longitude = getOptionalNumber(weather, "longitude") ?: 0.0F)
    return WeatherData(location = locationIdentifier,
                       timestamp = Date(timestamp),
                       temperature = temperature.toFloat(),
                       humidity = humidity.toFloat(),
                       localtime = if (weather.has("localtime")) weather.get("localtime") as String else "",
                       insideTemperature = getOptionalNumber(weather, "inside_temperature"),
                       insideHumidity = getOptionalNumber(weather, "inside_humidity"),
                       barometer = getOptionalNumber(weather, "barometer"),
                       solarradiation = getOptionalNumber(weather, "solarradiation"),
                       UV = getOptionalNumber(weather, "UV"),
                       watt = getOptionalNumber(weather, "watt"),
                       powerProduction=getOptionalNumber(weather, "powerProduction"),
                       powerFeed=getOptionalNumber(weather, "powerFeed"),
                       rain = getOptionalNumber(weather, "rain"),
                       rainToday = getOptionalNumber(weather, "rain_today"),
                       wind = getOptionalNumber(weather, "wind"),
                       isRaining = weather.has("raining") && weather.getBoolean("raining"),
                       position = position)
  }

  @Throws(JSONException::class)
  private fun parseLocationData(location: WeatherLocation, weather: JSONObject) {
    location.name = weather.getString("location")
    location.shortName = weather.getString("location_short")
    if (weather.has("forecast")) {
      location.forecastUrl = Uri.parse(weather.getString("forecast"))
    }
  }

  private fun getOptionalNumber(json: JSONObject, name: String): Float? {
    return if (json.has(name)) (json.get(name) as Number).toFloat() else null
  }

  fun fetchStatisticsFromUrl(locationIds: List<String>): HashMap<String, Statistics> {
    if (locationIds.size > 0) {
      var concatenatedLocationIds = locationIds[0]
      for (i in 1 until locationIds.size) {
        concatenatedLocationIds += "," + locationIds[i]
      }
      val jsonStatistics = RawDataFetcher.getStringFromUrl(
          "https://wettercentral.appspot.com/weatherstation/read?build=" + buildNumber +
              "&locations=" + concatenatedLocationIds + "&type=stats")
      try {
        return JsonTranslator.toStatistics(jsonStatistics.data)
      } catch (e: Throwable) {
        Log.e(WeatherDataFetcher::class.java.toString(), "Failed to parse JSON String <$jsonStatistics>", e)
      }

    }
    return HashMap()
  }

}
