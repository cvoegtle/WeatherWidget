package org.voegtle.weatherwidget.notification


import org.voegtle.weatherwidget.location.LocationIdentifier

import java.util.Date

data class WeatherAlert(var location: LocationIdentifier, var lastUpdate: Date)
