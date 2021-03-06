package org.voegtle.weatherwidget.notification


import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import org.voegtle.weatherwidget.R
import org.voegtle.weatherwidget.WeatherActivity
import org.voegtle.weatherwidget.data.WeatherData
import org.voegtle.weatherwidget.location.LocationIdentifier
import org.voegtle.weatherwidget.location.LocationSorter
import org.voegtle.weatherwidget.location.WeatherLocation
import org.voegtle.weatherwidget.preferences.ApplicationSettings
import org.voegtle.weatherwidget.util.DataFormatter
import org.voegtle.weatherwidget.util.FetchAllResponse
import org.voegtle.weatherwidget.util.DateUtil
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*


class NotificationSystemManager(private val context: Context, private val configuration: ApplicationSettings) {
  private val ALERT_ID = 1
  val INFO_ID = 2
  private val CHANNEL_ID = "wetterwolke"

  private val res: Resources = context.resources
  private val locationSorter = LocationSorter(context)
  private val notificationManager: NotificationManager = context.getSystemService(
      Context.NOTIFICATION_SERVICE) as NotificationManager

  private val stationCheck: WeatherStationCheck = WeatherStationCheck(configuration)
  private val dataFormatter = DataFormatter()

  init {
    if (Build.VERSION.SDK_INT >= 26) {
      setupNotificationChannel()
    }
  }

  fun checkDataForAlert(data: FetchAllResponse) {
    if (data.valid) {
      showAlertNotification(stationCheck.checkForOverdueStations(data.weatherMap))
      showInfoNotification(data.weatherMap)
    }
  }

  private fun showAlertNotification(alerts: List<WeatherAlert>) {
    if (alerts.isEmpty()) {
      notificationManager.cancel(ALERT_ID)
    } else {
      val notificationBuilder = NotificationCompat.Builder(context)
      notificationBuilder.setSmallIcon(R.drawable.wetterlogo)

      val bm = BitmapFactory.decodeResource(res, R.drawable.wetterlogo_alert)
      notificationBuilder.setLargeIcon(bm)

      notificationBuilder.setContentTitle(res.getString(R.string.data_overdue))
      if (Build.VERSION.SDK_INT >= 26) {
        notificationBuilder.setChannelId(CHANNEL_ID)
      }

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

  private fun showInfoNotification(data: HashMap<LocationIdentifier, WeatherData>) {
    if (!configuration.isShowInfoNotification) {
      notificationManager.cancel(INFO_ID)
    } else {
      val notificationBuilder = Notification.Builder(context)
      notificationBuilder.setSmallIcon(R.drawable.wetterlogo)

      val bm = BitmapFactory.decodeResource(res, R.drawable.wetterlogo)
      notificationBuilder.setLargeIcon(bm)

      notificationBuilder.setContentTitle("${res.getString(R.string.app_name)} - ${DateUtil.currentTime}")
      if (Build.VERSION.SDK_INT >= 26) {
        notificationBuilder.setChannelId(CHANNEL_ID)
      }

      val contentText = buildCurrentWeather(data)
      notificationBuilder.setContentText(contentText)

      val intentOpenApp = Intent(context, WeatherActivity::class.java)
      val pendingOpenApp = PendingIntent.getActivity(context, 0, intentOpenApp, PendingIntent.FLAG_UPDATE_CURRENT)
      notificationBuilder.setContentIntent(pendingOpenApp)

      val notification = notificationBuilder.build()
      notification.flags = notification.flags or Notification.FLAG_ONLY_ALERT_ONCE
      notification.flags = notification.flags or Notification.FLAG_ONGOING_EVENT
      notificationManager.notify(INFO_ID, notification)
    }
  }

  @TargetApi(26)
  fun createActivityNotification(): Notification {
    val notificationBuilder = Notification.Builder(context)
    notificationBuilder.setSmallIcon(R.drawable.wetterlogo)

    val bm = BitmapFactory.decodeResource(res, R.drawable.wetterlogo)
    notificationBuilder.setLargeIcon(bm)

    notificationBuilder.setContentTitle(res.getString(R.string.app_name))
    notificationBuilder.setChannelId(CHANNEL_ID)

    notificationBuilder.setContentText(res.getString(R.string.wetterwolke_in_background))

    val intentOpenApp = Intent(context, WeatherActivity::class.java)
    intentOpenApp.action = WeatherActivity.ANDROID8
    val pendingOpenApp = PendingIntent.getActivity(context, 0, intentOpenApp, PendingIntent.FLAG_UPDATE_CURRENT)
    notificationBuilder.setContentIntent(pendingOpenApp)

    val notification = notificationBuilder.build()
    notification.flags = notification.flags or Notification.FLAG_ONLY_ALERT_ONCE

    return notification
  }

  private fun buildCurrentWeather(data: HashMap<LocationIdentifier, WeatherData>): String {

    val relevantData = HashMap<LocationIdentifier, WeatherData>()
    configuration.locations
        .filter { it.preferences.showInWidget }
        .forEach { (key) ->
          data[key]?.let { relevantData.put(key, it) }
        }

    val weatherText = StringBuilder()

    val sortedData = locationSorter.sort(relevantData)
    sortedData.forEach {
      val location = configuration.findLocation(it.location)
      val weatherData = it
      location?.let { describeLocation(weatherText, it, weatherData) }
    }

    return weatherText.substring(0, weatherText.length - 3)
  }

  private fun describeLocation(weatherText: StringBuilder, location: WeatherLocation, weatherData: WeatherData) {
    weatherText.append("${location.shortName}: ${dataFormatter.formatTemperature(weatherData.temperature)}")
    weatherData.insideTemperature?.let {
      weatherText.append(", ${dataFormatter.formatTemperature(weatherData.insideTemperature)}")
    }
    weatherData.rainToday?.let {
      weatherText.append(", ${dataFormatter.formatRain(it)}")
    }

    weatherData.solarradiation?.let {
      if (weatherData.rainToday == null && weatherData.solarradiation > 0.0) {
        weatherText.append(", ${dataFormatter.formatSolarradiation(it)}")
      }
    }

    weatherText.append(" | ")
  }


  @TargetApi(26)
  private fun setupNotificationChannel() {
    // The id of the channel.
    val name = res.getString(R.string.channel_name)
    val description = res.getString(R.string.channel_description)
    val importance = NotificationManager.IMPORTANCE_LOW
    val channel = NotificationChannel(CHANNEL_ID, name, importance)

    channel.description = description
    channel.enableLights(false)
    channel.enableVibration(false)
    notificationManager.createNotificationChannel(channel)
  }


}
