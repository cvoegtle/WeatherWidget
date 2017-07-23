package org.voegtle.weatherwidget.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.widget.LinearLayout
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import com.google.android.gms.location.LocationServices
import org.voegtle.weatherwidget.R
import org.voegtle.weatherwidget.data.WeatherData
import org.voegtle.weatherwidget.preferences.ApplicationSettings
import org.voegtle.weatherwidget.util.UserFeedback
import java.util.*

class LocationContainer(val context: Context, private val container: LinearLayout, configuration: ApplicationSettings) {

  private val locationOrderStore: LocationOrderStore = LocationOrderStore(context)
  private val locations: List<WeatherLocation> = configuration.locations
  private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
  private var userPosition: Position = Position(latitude = 51.7238851F, longitude = 8.7589337F) // Paderborn

  fun updateLocationOrder(weatherData: HashMap<LocationIdentifier, WeatherData>) {
    updateLocation()
    val sortedWeatherData = sort(weatherData)

    for (i in sortedWeatherData.indices) {
      val data = sortedWeatherData[i]
      var view = container.getChildAt(i) as LocationView
      if (!belongTogether(data, view)) {
        view = moveViewToPosition(i, data)
      }
      manageViewPosition(view, i)
    }

  }

  private fun updateLocation() {
    val permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
    if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
      return
    }
    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
      location?.let {
        this.userPosition = Position(latitude = location.latitude.toFloat(),
            longitude = location.longitude.toFloat())
      }
    }
  }

  private fun requestLocationPermission() {
    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), 0)
  }

  override fun onRequestPermissionsResult(diagramId: Int, permissions: Array<String>, grantResults: IntArray) {
    if (grantResults.isNotEmpty()) {
      if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        updateLocation()
      } else {
        UserFeedback(this).showMessage(R.string.message_permission_required, true)
      }
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


  private fun findLocation(data: WeatherData): WeatherLocation {
    return locations.first { it.key == data.location }
  }

  private fun findLocationView(data: WeatherData): LocationView {
    val location = findLocation(data)
    return container.findViewById(location.weatherViewId) as LocationView
  }

  private fun manageViewPosition(view: LocationView, position: Int) {
    val oldPosition = locationOrderStore.readIndexOf(view.id)
    locationOrderStore.writeIndexOf(view.id, position)
    view.highlight(position < oldPosition)
  }


  private fun sort(weatherData: HashMap<LocationIdentifier, WeatherData>): ArrayList<WeatherData> {
    val sortedWeatherData = ArrayList<WeatherData>()
    sortedWeatherData.addAll(weatherData.values)
    val comparator = LocationComparatorFactory.createComparator(locationOrderStore.readOrderCriteria(), userPosition)
    Collections.sort(sortedWeatherData, comparator)
    return sortedWeatherData
  }
}
