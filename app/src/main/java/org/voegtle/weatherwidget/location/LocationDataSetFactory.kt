package org.voegtle.weatherwidget.location

import android.content.Context
import org.voegtle.weatherwidget.cache.StateCache
import org.voegtle.weatherwidget.data.Statistics
import org.voegtle.weatherwidget.data.WeatherData
import org.voegtle.weatherwidget.preferences.OrderCriteria
import org.voegtle.weatherwidget.util.DataFormatter
import org.voegtle.weatherwidget.util.JsonTranslator

class LocationDataSetFactory(context: Context) {
    private val stateCache = StateCache(context)
    private val locationOrderStore = LocationOrderStore(context)
    private val formatter = DataFormatter()

    fun assembleLocationDataSets(
        locations: List<WeatherLocation>,
        data: Map<LocationIdentifier, WeatherData>
    ): List<LocationDataSet> {
        val locationDataSets = ArrayList<LocationDataSet>()

        for (location in locations) {
            val weatherData = data[location.key]
            if (weatherData != null) {
                locationDataSets.add(LocationDataSet(
                    weatherLocation = location,
                    caption = appendTimeAndDistance(location.name, weatherData),
                    weatherData = weatherData,
                    statistics = lookupStatistics(location.key)))
            }
        }
        return locationDataSets
    }

    private fun lookupStatistics(locationIdentifier: LocationIdentifier): Statistics? {
            val state = stateCache.read(locationIdentifier)
            return if (state.isExpanded) JsonTranslator.toSingleStatistics(state.statistics) else null
    }

    private fun appendTimeAndDistance(locationName: String, data: WeatherData): String {
        var caption = "$locationName - ${data.localtime}"

        if (locationOrderStore.readOrderCriteria() == OrderCriteria.location) {
            val userPosition = locationOrderStore.readPosition()
            val distance = userPosition.distanceTo(data.position)
            caption += " - ${formatter.formatDistance(distance.toFloat())}"
        }

        return caption
    }

}