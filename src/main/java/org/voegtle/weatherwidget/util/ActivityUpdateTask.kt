package org.voegtle.weatherwidget.util

import android.os.AsyncTask
import android.util.Log
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.activity_weather.*
import org.voegtle.weatherwidget.R
import org.voegtle.weatherwidget.WeatherActivity
import org.voegtle.weatherwidget.data.WeatherData
import org.voegtle.weatherwidget.location.*
import org.voegtle.weatherwidget.notification.NotificationSystemManager
import org.voegtle.weatherwidget.preferences.ApplicationSettings
import org.voegtle.weatherwidget.widget.ScreenPainterFactory
import org.voegtle.weatherwidget.widget.WidgetScreenPainter

import java.util.ArrayList
import java.util.HashMap

class ActivityUpdateTask internal constructor(private val activity: WeatherActivity, private val configuration: ApplicationSettings, private val showToast: Boolean) : AsyncTask<Void, Void, HashMap<LocationIdentifier, WeatherData>>() {
  private val weatherDataFetcher = WeatherDataFetcher(ContextUtil.getBuildNumber(activity))
  private val userLocationUpdater = UserLocationUpdater(activity.applicationContext)

  override fun doInBackground(vararg voids: Void): HashMap<LocationIdentifier, WeatherData> {
    userLocationUpdater.updateLocation()
    return weatherDataFetcher.fetchAllWeatherDataFromServer(configuration.locations, configuration.secret!!)
  }

  override fun onPostExecute(data: HashMap<LocationIdentifier, WeatherData>) {
    try {
      updateViewData(data)
      sortViews(data)
      updateWidgets(data)

      UserFeedback(activity).showMessage(R.string.message_data_updated, showToast)

      val notificationManager = NotificationSystemManager(activity, configuration)
      notificationManager.checkDataForAlert(data)
    } catch (th: Throwable) {
      UserFeedback(activity).showMessage(R.string.message_data_update_failed, true)
      Log.e(ActivityUpdateTask::class.java.toString(), "Failed to update View", th)
    }

  }

  private fun updateWidgets(data: HashMap<LocationIdentifier, WeatherData>) {
    val factory = ScreenPainterFactory(activity, configuration)
    val screenPainters = factory.createScreenPainters()
    for (screenPainter in screenPainters) {
      screenPainter.updateWidgetData(data)
      screenPainter.showDataIsValid()
    }
  }

  private fun updateViewData(data: HashMap<LocationIdentifier, WeatherData>) {
    configuration.locations.forEach { location ->
      data[location.key]?.let {
        updateWeatherLocation(location.weatherViewId, location.name, it)
      }
    }
  }

  private fun updateWeatherLocation(locationId: Int, locationName: String, data: WeatherData) {
    val contentView = activity.findViewById(locationId) as LocationView

    val color = ColorUtil.byAge(configuration.colorScheme, data.timestamp)
    val caption = getCaption(locationName, data)

    updateView(contentView, caption, data, color)
  }

  private fun getCaption(locationName: String, data: WeatherData): String {
    return "$locationName - ${data.localtime}"
  }

  private fun updateView(view: LocationView, caption: String, data: WeatherData, color: Int) {
    view.setCaption(caption)
    view.setData(data)
    view.setTextColor(color)
  }

  private fun sortViews(data: HashMap<LocationIdentifier, WeatherData>) {
    val container = activity.location_container
    val locationContainer = LocationContainer(activity.applicationContext, container, configuration)
    locationContainer.updateLocationOrder(data)
  }

}
