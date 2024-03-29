package org.voegtle.weatherwidget.location

import android.content.Context
import android.widget.LinearLayout
import org.voegtle.weatherwidget.data.WeatherData
import org.voegtle.weatherwidget.preferences.ApplicationSettings

class LocationContainer(val context: Context, private val container: LinearLayout, configuration: ApplicationSettings) {

  private val locationOrderStore = LocationOrderStore(context)
  private val locations: List<WeatherLocation> = configuration.locations
  private val locationSorter = LocationSorter(context)

  fun updateLocationOrder(weatherData: HashMap<LocationIdentifier, WeatherData>) {
    val sortedWeatherData = locationSorter.sort(weatherData)

    for (i in sortedWeatherData.indices) {
      val data = sortedWeatherData[i]
      var view = container.getChildAt(i) as LocationView
      if (!belongTogether(data, view)) {
        view = moveViewToPosition(i, data)
      }
      manageViewPosition(view, i)
    }

  }

  private fun belongTogether(data: WeatherData, view: LocationView): Boolean {
    val location = findLocation(data)
    return location.weatherViewId == view.id
  }

  private fun moveViewToPosition(i: Int, data: WeatherData): LocationView {
    val view = findLocationView(data)
    container.removeView(view)
    container.addView(view, i)
    return view
  }


  private fun findLocationView(data: WeatherData): LocationView {
    val location = findLocation(data)
    return container.findViewById(location.weatherViewId)
  }

  private fun findLocation(data: WeatherData): WeatherLocation {
    return locations.first { it.key == data.location }
  }

  private fun manageViewPosition(view: LocationView, position: Int) {
    val oldPosition = locationOrderStore.readIndexOf(view.id)
    locationOrderStore.writeIndexOf(view.id, position)
    view.highlight(position < oldPosition)
  }

}
