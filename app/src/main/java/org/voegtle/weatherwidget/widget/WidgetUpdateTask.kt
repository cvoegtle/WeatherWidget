package org.voegtle.weatherwidget.widget

import android.content.Context
import android.util.Log
import org.voegtle.weatherwidget.R
import org.voegtle.weatherwidget.location.UserLocationUpdater
import org.voegtle.weatherwidget.preferences.ApplicationSettings
import org.voegtle.weatherwidget.util.FetchAllResponse
import org.voegtle.weatherwidget.util.UserFeedback

class WidgetUpdateTask(private val context: Context, configuration: ApplicationSettings,
                       private val screenPainters: ArrayList<WidgetScreenPainter>)
  : AbstractWidgetUpdateTask<Void, Void, FetchAllResponse>(context, configuration) {
  private val userLocationUpdater = UserLocationUpdater(context)

  @Deprecated("Deprecated in Java")
  override fun onPreExecute() {
    screenPainters.forEach { screenPainter ->
      screenPainter.showDataIsInvalid()
    }
  }


  @Deprecated("Deprecated in Java")
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


  @Deprecated("Deprecated in Java")
  override fun onPostExecute(response: FetchAllResponse) {
    try {
      if (response.valid) {
        screenPainters.forEach { screenPainter -> screenPainter.updateWidgetData(response.weatherMap) }
        updateNotification(response)
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
