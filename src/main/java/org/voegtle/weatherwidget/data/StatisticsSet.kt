package org.voegtle.weatherwidget.data

class StatisticsSet(range: Statistics.TimeRange) {
    var range: Statistics.TimeRange
        internal set
    var rain: Float? = null
    var maxTemperature: Float? = null
    var minTemperature: Float? = null
    var kwh: Float? = null

    init {
        this.range = range
    }
}
