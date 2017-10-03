package org.voegtle.weatherwidget.widget

import android.content.Context
import android.util.Log
import org.voegtle.weatherwidget.data.WeatherData
import org.voegtle.weatherwidget.location.LocationIdentifier
import org.voegtle.weatherwidget.location.UserLocationUpdater
import org.voegtle.weatherwidget.preferences.ApplicationSettings
import java.util.*

class WidgetUpdateTask(context: Context, configuration: ApplicationSettings,
                       private val screenPainters: ArrayList<WidgetScreenPainter>)
  : AbstractWidgetUpdateTask<Void, Void, HashMap<LocationIdentifier, WeatherData>>(context, configuration) {
  private val userLocationUpdater = UserLocationUpdater(context)

  override fun onPreExecute() {
    screenPainters.forEach { screenPainter ->
      screenPainter.showDataIsInvalid()
      screenPainter.updateAllWidgets()
    }
  }


  override fun doInBackground(vararg voids: Void): HashMap<LocationIdentifier, WeatherData> {
    try {
      if (!screenPainters.isEmpty()) {
        return fetchAllWeatherData()
      }
    } catch (ignore: Throwable) {
    }

    return HashMap()
  }

  protected fun fetchAllWeatherData(): HashMap<LocationIdentifier, WeatherData> {
    userLocationUpdater.updateLocation()
    return weatherDataFetcher.fetchAllWeatherDataFromServer(configuration.locations, configuration.secret!!)
  }


  override fun onPostExecute(data: HashMap<LocationIdentifier, WeatherData>) {
    try {
      screenPainters.forEach { screenPainter -> screenPainter.updateWidgetData(data) }
      checkDataForAlert(data)
    } catch (th: Throwable) {
      Log.e(WidgetUpdateTask::class.java.toString(), "Failed to update View", th)
    } finally {
      showDataIsValid()
    }
  }

  private fun showDataIsValid() {
    try {
      screenPainters.forEach { screenPainter -> screenPainter.showDataIsValid() }
    } catch (th: Throwable) {
      Log.e(WidgetUpdateTask::class.java.toString(), "Failed to repaint view", th)
    }

  }
}
