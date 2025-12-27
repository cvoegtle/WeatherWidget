package org.voegtle.weatherwidget.notification


import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.BitmapFactory
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import org.voegtle.weatherwidget.R
import org.voegtle.weatherwidget.data.WeatherData
import org.voegtle.weatherwidget.location.LocationDataSetFactory
import org.voegtle.weatherwidget.location.LocationIdentifier
import org.voegtle.weatherwidget.location.LocationSorter
import org.voegtle.weatherwidget.location.WeatherLocation
import org.voegtle.weatherwidget.preferences.ApplicationPreferences
import org.voegtle.weatherwidget.system.IntentFactory
import org.voegtle.weatherwidget.util.DataFormatter
import org.voegtle.weatherwidget.util.DateUtil
import org.voegtle.weatherwidget.util.FetchAllResponse
import kotlin.math.max


class NotificationSystemManager(private val context: Context, private val configuration: ApplicationPreferences) {
    private val INFO_ID = 2
    private val CHANNEL_ID = "wetterwolke"

    private val res: Resources = context.resources
    private val locationSorter = LocationSorter(context)
    private val locationDataSetFactory = LocationDataSetFactory(context)

    private val notificationManager: NotificationManager = context.getSystemService(
        Context.NOTIFICATION_SERVICE
    ) as NotificationManager

    private val dataFormatter = DataFormatter()

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setupNotificationChannel()
        }
    }

    fun updateNotification(data: FetchAllResponse) {
        if (data.valid) {
            showInfoNotification(data.weatherMap)
        }
    }


    private fun showInfoNotification(data: HashMap<LocationIdentifier, WeatherData>) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                return
            }
        }

        val notificationBuilder = Notification.Builder(context)
        notificationBuilder.setSmallIcon(R.drawable.wetterlogo)

        val bm = BitmapFactory.decodeResource(res, R.drawable.wetterlogo)
        notificationBuilder.setLargeIcon(bm)

        notificationBuilder.setContentTitle("${res.getString(R.string.app_name)} - ${DateUtil.currentTime}")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationBuilder.setChannelId(CHANNEL_ID)
        }

        val contentText = buildCurrentWeather(data)
        notificationBuilder.setContentText(contentText)

        val pendingOpenApp = IntentFactory.createRefreshIntent(context)
        notificationBuilder.setContentIntent(pendingOpenApp)

        val notification = notificationBuilder.build()
        notification.flags = notification.flags or Notification.FLAG_ONLY_ALERT_ONCE
        notification.flags = notification.flags or Notification.FLAG_ONGOING_EVENT
        notificationManager.notify(INFO_ID, notification)
    }

    private fun buildCurrentWeather(data: HashMap<LocationIdentifier, WeatherData>): String {

        val relevantData = HashMap<LocationIdentifier, WeatherData>()
        configuration.locations
            .filter { it.preferences.showInWidget }
            .forEach { (key) ->
                data[key]?.let { relevantData[key] = it }
            }

        val weatherText = StringBuilder()

        val relevantDataSets = locationDataSetFactory.assembleLocationDataSets(configuration.locations, relevantData)
        locationSorter.sort(relevantDataSets)
        relevantDataSets.forEach { dataSet ->
            val location = configuration.findLocation(dataSet.weatherData.location)
            location?.let { currentLocation -> describeLocation(weatherText, currentLocation, dataSet.weatherData) }
        }

        return weatherText.substring(0, max(weatherText.length - 3, 0))
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
            if (weatherData.rainToday == null && it > 0.0) {
                weatherText.append(", ${dataFormatter.formatSolarradiation(it)}")
            }
        }

        weatherText.append(" | ")
    }


    @RequiresApi(26)
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
