package org.voegtle.weatherwidget.data

import org.voegtle.weatherwidget.location.WeatherLocation

data class LocationDataSet(val weatherLocation: WeatherLocation, var caption: String, val weatherData: WeatherData, var statistics: Statistics?)
