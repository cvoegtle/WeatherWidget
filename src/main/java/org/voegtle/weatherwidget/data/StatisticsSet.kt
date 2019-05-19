package org.voegtle.weatherwidget.data

data class StatisticsSet(val range: Statistics.TimeRange,
                         val rain: Float?,
                         val minTemperature: Float?,
                         val maxTemperature: Float?,
                         val solarRadiationMax: Float?,
                         val kwh: Float?)
