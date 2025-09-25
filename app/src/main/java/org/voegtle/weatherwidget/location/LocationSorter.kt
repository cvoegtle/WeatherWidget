package org.voegtle.weatherwidget.location

import android.content.Context
import org.voegtle.weatherwidget.data.WeatherData
import java.util.Collections

class LocationSorter(context: Context) {
  private val locationOrderStore = LocationOrderStore(context)

  fun sort(locationData: List<LocationDataSet>) {
    val comparator = LocationComparatorFactory.createComparator(locationOrderStore.readOrderCriteria(),
        locationOrderStore.readPosition())
    Collections.sort(locationData, comparator)
  }
}
