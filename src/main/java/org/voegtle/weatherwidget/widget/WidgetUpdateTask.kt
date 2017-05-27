package org.voegtle.weatherwidget.widget

import android.content.Context
import android.util.Log
import org.voegtle.weatherwidget.data.WeatherData
import org.voegtle.weatherwidget.location.LocationIdentifier
import org.voegtle.weatherwidget.preferences.ApplicationSettings

import java.util.ArrayList
import java.util.HashMap

class WidgetUpdateTask(context: Context, configuration: ApplicationSettings, private val screenPainters: ArrayList<WidgetScreenPainter>) : AbstractWidgetUpdateTask<Void, Void, HashMap<LocationIdentifier, WeatherData>>(context, configuration) {

  override fun onPreExecute() {
    for (screenPainter in screenPainters) {
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
    return weatherDataFetcher.fetchAllWeatherDataFromServer(configuration.locations, configuration.secret!!)
  }


  override fun onPostExecute(data: HashMap<LocationIdentifier, WeatherData>) {
    try {
      for (screenPainter in screenPainters) {
        screenPainter.updateWidgetData(data)
      }
      checkDataForAlert(data)
    } catch (th: Throwable) {
      Log.e(WidgetUpdateTask::class.java.toString(), "Failed to update View", th)
    } finally {
      showDataIsValid()
    }
  }

  private fun showDataIsValid() {
    try {
      for (screenPainter in screenPainters) {
        screenPainter.showDataIsValid()
      }
    } catch (th: Throwable) {
      Log.e(WidgetUpdateTask::class.java.toString(), "Failed to repaint view", th)
    }

  }
}
