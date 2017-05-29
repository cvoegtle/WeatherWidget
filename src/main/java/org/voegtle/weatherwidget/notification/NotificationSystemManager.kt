package org.voegtle.weatherwidget.notification


import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import org.voegtle.weatherwidget.R
import org.voegtle.weatherwidget.WeatherActivity
import org.voegtle.weatherwidget.data.WeatherData
import org.voegtle.weatherwidget.location.LocationIdentifier
import org.voegtle.weatherwidget.location.WeatherLocation
import org.voegtle.weatherwidget.preferences.ApplicationSettings
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

class NotificationSystemManager(private val context: Context, private val configuration: ApplicationSettings) {

  private val res: Resources
  private val notificationManager: NotificationManager
  private val stationCheck: WeatherStationCheck
  private val numberFormat: DecimalFormat


  init {
    this.res = context.resources
    this.notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    this.stationCheck = WeatherStationCheck(configuration)
    this.numberFormat = NumberFormat.getNumberInstance(Locale.GERMANY) as DecimalFormat
    this.numberFormat.applyPattern("###.#")
  }

  fun checkDataForAlert(data: HashMap<LocationIdentifier, WeatherData>) {
    if (data.size > 0) {
      showAlertNotification(stationCheck.checkForOverdueStations(data))
      showInfoNotifcation(data)
    }
  }

  private fun showAlertNotification(alerts: List<WeatherAlert>) {
    if (alerts.isEmpty()) {
      notificationManager.cancel(ALERT_ID)
    } else {
      val notificationBuilder = Notification.Builder(context)
      notificationBuilder.setSmallIcon(R.drawable.wetterlogo)

      val bm = BitmapFactory.decodeResource(res, R.drawable.wetterlogo_alert)
      notificationBuilder.setLargeIcon(bm)

      notificationBuilder.setContentTitle(res.getString(R.string.data_overdue))

      val contentText = buildMessage(alerts)
      notificationBuilder.setContentText(contentText)

      val intentOpenApp = Intent(context, WeatherActivity::class.java)
      val pendingOpenApp = PendingIntent.getActivity(context, 0, intentOpenApp, PendingIntent.FLAG_UPDATE_CURRENT)
      notificationBuilder.setContentIntent(pendingOpenApp)

      val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
      notificationBuilder.setSound(alarmSound)
      val notification = notificationBuilder.build()
      notification.flags = notification.flags or Notification.FLAG_ONLY_ALERT_ONCE
      notificationManager.notify(ALERT_ID, notification)
    }
  }

  private fun buildMessage(alerts: List<WeatherAlert>): String {
    val contentText = StringBuilder()
    for ((location, lastUpdate) in alerts) {
      contentText.append(location)
          .append(" ").append(res.getString(R.string.since))
          .append(" ").append(formatTime(lastUpdate))
          .append(" ").append(res.getString(R.string.overdue)).append(".\n")
    }
    return contentText.substring(0, contentText.length - 1)
  }

  private fun formatTime(lastUpdate: Date): String {
    val timeInMinutes = (Date().time - lastUpdate.time) / (60 * 1000)
    if (timeInMinutes < 120) {
      return timeInMinutes.toString() + " " + res.getString(R.string.minutes)
    }
    if (timeInMinutes < 48 * 60) {
      return (timeInMinutes / 60).toString() + " " + res.getString(R.string.hours)
    }

    return (timeInMinutes / (24 * 60)).toString() + " " + res.getString(R.string.days)
  }

  private fun showInfoNotifcation(data: HashMap<LocationIdentifier, WeatherData>) {
    if (!configuration.isShowInfoNotification) {
      notificationManager.cancel(INFO_ID)
    } else {
      val notificationBuilder = Notification.Builder(context)
      notificationBuilder.setSmallIcon(R.drawable.wetterlogo)

      val bm = BitmapFactory.decodeResource(res, R.drawable.wetterlogo)
      notificationBuilder.setLargeIcon(bm)

      notificationBuilder.setContentTitle(res.getString(R.string.app_name))

      val contentText = buildCurrentWeather(data)
      notificationBuilder.setContentText(contentText)

      val intentOpenApp = Intent(context, WeatherActivity::class.java)
      val pendingOpenApp = PendingIntent.getActivity(context, 0, intentOpenApp, PendingIntent.FLAG_UPDATE_CURRENT)
      notificationBuilder.setContentIntent(pendingOpenApp)

      val notification = notificationBuilder.build()
      notification.flags = notification.flags or Notification.FLAG_ONLY_ALERT_ONCE
      notification.flags = notification.flags or Notification.FLAG_ONGOING_EVENT
      notification.priority = Notification.PRIORITY_HIGH
      notificationManager.notify(INFO_ID, notification)
    }
  }

  private fun buildCurrentWeather(data: HashMap<LocationIdentifier, WeatherData>): String {
    val weatherText = StringBuilder()

    for (location in configuration.locations) {
      if (location.preferences.showInWidget) {
        val weatherData = data[location.key]
        if (weatherData != null) {
          describeLocation(weatherText, location, weatherData)
          weatherText.append(" | ")
        }
      }
    }

    return weatherText.substring(0, weatherText.length - 3)
  }

  private fun describeLocation(weatherText: StringBuilder, location: WeatherLocation, weatherData: WeatherData) {
    weatherText.append(location.shortName)
    weatherText.append(": ")
    weatherText.append(numberFormat.format(weatherData.temperature)).append("°C")
    if (weatherData.insideTemperature != null) {
      weatherText.append(", ").append(numberFormat.format(weatherData.insideTemperature)).append("°C")
    }
    if (weatherData.rainToday != null) {
      weatherText.append(", ").append(numberFormat.format(weatherData.rainToday)).append("l")
    }
  }

  companion object {
    private val ALERT_ID = 1
    private val INFO_ID = 2
  }


}