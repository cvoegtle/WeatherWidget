package org.voegtle.weatherwidget.util

import android.os.AsyncTask
import android.util.Log
import kotlinx.android.synthetic.main.activity_weather.*
import org.voegtle.weatherwidget.R
import org.voegtle.weatherwidget.WeatherActivity
import org.voegtle.weatherwidget.data.WeatherData
import org.voegtle.weatherwidget.location.LocationContainer
import org.voegtle.weatherwidget.location.LocationIdentifier
import org.voegtle.weatherwidget.location.LocationOrderStore
import org.voegtle.weatherwidget.location.LocationView
import org.voegtle.weatherwidget.location.UserLocationUpdater
import org.voegtle.weatherwidget.location.WeatherLocation
import org.voegtle.weatherwidget.notification.NotificationSystemManager
import org.voegtle.weatherwidget.preferences.ApplicationSettings
import org.voegtle.weatherwidget.preferences.OrderCriteria
import org.voegtle.weatherwidget.widget.ScreenPainterFactory
import java.util.HashMap

class ActivityUpdateTask internal constructor(private val activity: WeatherActivity,
                                              private val configuration: ApplicationSettings,
                                              private val showToast: Boolean) : AsyncTask<Void, Void, FetchAllResponse>() {
  private val weatherDataFetcher = WeatherDataFetcher(ContextUtil.getBuildNumber(activity))
  private val userLocationUpdater = UserLocationUpdater(activity.applicationContext)
  private val locationOrderStore = LocationOrderStore(activity.applicationContext)
  private val formatter = DataFormatter()

  override fun doInBackground(vararg voids: Void): FetchAllResponse {
    userLocationUpdater.updateLocation()
    return weatherDataFetcher.fetchAllWeatherDataFromServer(configuration.locations, configuration.secret!!)
  }

  override fun onPostExecute(response: FetchAllResponse) {
    try {
      updateViewData(response.weatherMap)
      sortViews(response.weatherMap)
      updateWidgets(response.weatherMap)

      UserFeedback(activity).showMessage(
          if (response.valid) R.string.message_data_updated else R.string.message_data_update_failed, showToast)

      val notificationManager = NotificationSystemManager(activity, configuration)
      notificationManager.checkDataForAlert(response)
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
        updateWeatherLocation(location, location.name, it)
      }
    }
  }

  private fun updateWeatherLocation(location: WeatherLocation, locationName: String, data: WeatherData) {
    val contentView: LocationView = activity.findViewById(location.weatherViewId)

    val highlight = location.preferences.highlightActive
    val colorScheme = configuration.colorScheme
    val color = if (highlight && !DateUtil.isOutdated(data.timestamp))
      ColorUtil.highlightText(colorScheme)
    else
      ColorUtil.byAge(colorScheme, data.timestamp)

    val caption = getCaption(locationName, data)

    updateView(contentView, caption, data, color)
  }

  private fun getCaption(locationName: String, data: WeatherData): String {
    var caption = "$locationName - ${data.localtime}"

    if (locationOrderStore.readOrderCriteria() == OrderCriteria.location) {
      val userPosition = locationOrderStore.readPosition()
      val distance = userPosition.distanceTo(data.position)
      caption += " - ${formatter.formatDistance(distance.toFloat())}"
    }

    return caption
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
