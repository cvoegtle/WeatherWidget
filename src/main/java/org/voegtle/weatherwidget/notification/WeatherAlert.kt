package org.voegtle.weatherwidget.notification


import org.voegtle.weatherwidget.location.LocationIdentifier

import java.util.Date

data class WeatherAlert(val location: LocationIdentifier, val lastUpdate: Date)
