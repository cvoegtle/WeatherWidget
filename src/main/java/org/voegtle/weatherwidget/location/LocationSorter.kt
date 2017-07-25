package org.voegtle.weatherwidget.location

import android.content.Context
import org.voegtle.weatherwidget.data.WeatherData
import java.util.*

class LocationSorter(context: Context) {
  private val locationOrderStore = LocationOrderStore(context)

  fun sort(weatherData: HashMap<LocationIdentifier, WeatherData>): List<WeatherData> {
    val sortedWeatherData = ArrayList<WeatherData>(weatherData.values)
    val comparator = LocationComparatorFactory.createComparator(locationOrderStore.readOrderCriteria(),
        locationOrderStore.readPosition())
    Collections.sort(sortedWeatherData, comparator)
    return sortedWeatherData
  }
}