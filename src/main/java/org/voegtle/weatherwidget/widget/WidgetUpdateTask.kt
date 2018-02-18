package org.voegtle.weatherwidget.widget

import android.content.Context
import android.util.Log
import org.voegtle.weatherwidget.R
import org.voegtle.weatherwidget.location.UserLocationUpdater
import org.voegtle.weatherwidget.preferences.ApplicationSettings
import org.voegtle.weatherwidget.util.FetchAllResponse
import org.voegtle.weatherwidget.util.UserFeedback
import java.util.ArrayList

class WidgetUpdateTask(private val context: Context, configuration: ApplicationSettings,
                       private val screenPainters: ArrayList<WidgetScreenPainter>)
  : AbstractWidgetUpdateTask<Void, Void, FetchAllResponse>(context, configuration) {
  private val userLocationUpdater = UserLocationUpdater(context)

  override fun onPreExecute() {
    screenPainters.forEach { screenPainter ->
      screenPainter.showDataIsInvalid()
    }
  }


  override fun doInBackground(vararg voids: Void): FetchAllResponse {
    if (!screenPainters.isEmpty()) {
      return fetchAllWeatherData()
    }

    return FetchAllResponse(true, HashMap())
  }

  private fun fetchAllWeatherData(): FetchAllResponse {
    userLocationUpdater.updateLocation()
    return weatherDataFetcher.fetchAllWeatherDataFromServer(configuration.locations, configuration.secret!!)
  }


  override fun onPostExecute(response: FetchAllResponse) {
    try {
      if (response.valid) {
        screenPainters.forEach { screenPainter -> screenPainter.updateWidgetData(response.weatherMap) }
        checkDataForAlert(response)
      } else {
        UserFeedback(context).showMessage(R.string.message_data_update_failed, true)
      }
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
