package org.voegtle.weatherwidget.preferences

data class WidgetPreferences(val fontCorrectionFactor: Int,
                             val numberOfItems:Int,
                             val showTemperature: Boolean,
                             val showRain: Boolean,
                             val showRainLastHour: Boolean,
                             val showWindSpeed: Boolean,
                             val showWindGust: Boolean,
                             val showCurrentRadiation: Boolean,
                             val showHumidity: Boolean,
                             val showPressure: Boolean)