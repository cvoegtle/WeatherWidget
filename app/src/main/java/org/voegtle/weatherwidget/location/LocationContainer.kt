package org.voegtle.weatherwidget.location

import android.content.Context
import org.voegtle.weatherwidget.data.WeatherData
import org.voegtle.weatherwidget.preferences.ApplicationSettings

// Neue Datenklasse für das Ergebnis der Sortierung
data class SortResult(
    val sortedIdentifiers: List<LocationIdentifier>,
    val highlightedIdentifiers: Set<LocationIdentifier>
)

class LocationContainer(
    private val context: Context, // LinearLayout wurde entfernt
    private val configuration: ApplicationSettings
) {

    private val locationOrderStore = LocationOrderStore(context)
    private val locations: List<WeatherLocation> = configuration.locations
    private val locationSorter = LocationSorter(context)

    fun updateLocationOrder(weatherData: HashMap<LocationIdentifier, WeatherData>): SortResult {
        val sortedWeatherDataList: List<WeatherData> = locationSorter.sort(weatherData)

        val newSortedIdentifiers = mutableListOf<LocationIdentifier>()
        val newHighlightedIdentifiers = mutableSetOf<LocationIdentifier>()

        for ((index, data) in sortedWeatherDataList.withIndex()) {
            val locationIdentifier = data.location // Annahme: WeatherData hat 'location: LocationIdentifier'
            newSortedIdentifiers.add(locationIdentifier)

            if (determineAndStorePositionAndHighlight(locationIdentifier, index)) {
                newHighlightedIdentifiers.add(locationIdentifier)
            }
        }
        return SortResult(newSortedIdentifiers, newHighlightedIdentifiers)
    }

    private fun determineAndStorePositionAndHighlight(locationId: LocationIdentifier, newPosition: Int): Boolean {
        val locationKeyName = locationId.name // Verwende den Enum-Namen als String-Schlüssel

        val oldPosition = locationOrderStore.readIndexOfStringKey(locationKeyName) 
        locationOrderStore.writeIndexOfStringKey(locationKeyName, newPosition)

        return newPosition < oldPosition && oldPosition != LocationOrderStore.DEFAULT_INDEX 
    }

    private fun findLocation(data: WeatherData): WeatherLocation {
        return locations.first { it.key == data.location }
    }

    fun findLocationByIdentifier(identifier: LocationIdentifier): WeatherLocation? {
        return locations.firstOrNull { it.key == identifier }
    }
}
