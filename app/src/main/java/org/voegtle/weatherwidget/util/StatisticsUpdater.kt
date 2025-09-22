package org.voegtle.weatherwidget.util

import android.content.Context
import android.util.Log
import org.voegtle.weatherwidget.data.Statistics
import org.voegtle.weatherwidget.location.WeatherLocation
import org.voegtle.weatherwidget.state.State
import org.voegtle.weatherwidget.state.StateCache
import java.util.Date
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class StatisticsUpdater(private val context: Context) {
    private val stateCache = StateCache(context)
    // Annahme: ContextUtil.getBuildNumber kann mit Context umgehen oder wurde angepasst
    private val weatherDataFetcher = WeatherDataFetcher(ContextUtil.getBuildNumber(context))
    private val executor: ScheduledExecutorService = Executors.newScheduledThreadPool(1)

    companion object {
        private const val TAG = "StatisticsUpdater"
    }

    /**
     * Ruft Statistiken für einen bestimmten Standort ab.
     * Prüft zuerst den Cache. Wenn die Daten veraltet sind oder forceUpdate true ist,
     * werden neue Daten vom Server geladen.
     * Das Ergebnis wird über den onResult-Callback auf einem Hintergrundthread zurückgegeben.
     * Der Aufrufer ist dafür verantwortlich, bei Bedarf auf den UI-Thread zu wechseln.
     */
    fun fetchStatisticsForLocation(
        weatherLocation: WeatherLocation,
        forceUpdate: Boolean,
        onResult: (statistics: Statistics?) -> Unit
    ) {
        val locationKey = weatherLocation.key.id
        val cachedState = stateCache.read(locationKey)

        if (!forceUpdate && !cachedState.outdated() && cachedState.statistics.isNotEmpty()) {
            try {
                val cachedStats = JsonTranslator.toSingleStatistics(cachedState.statistics)
                onResult(cachedStats) // Sofortige Rückgabe der gültigen Cache-Daten
                return
            } catch (e: Exception) {
                Log.e(TAG, "Error deserializing cached statistics for $locationKey", e)
                // Cache ist korrupt, wie Force-Update behandeln
            }
        }

        executor.schedule({
            try {
                val fetchedStatisticsMap = weatherDataFetcher.fetchStatisticsFromUrl(arrayListOf(weatherLocation.identifier))
                val fetchedStats = fetchedStatisticsMap[weatherLocation.identifier]

                if (fetchedStats != null) {
                    updateCache(locationKey, fetchedStats)
                    onResult(fetchedStats)
                } else {
                    Log.w(TAG, "No statistics fetched for ${weatherLocation.identifier}, returning null or old cache.")
                    // Kein Fehler, aber keine Daten. Optional alte (veraltete) Cache-Daten zurückgeben, wenn gewünscht.
                    // Für dieses Beispiel geben wir null zurück, wenn nichts Neues geholt wurde.
                    onResult(null) 
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching statistics for ${weatherLocation.identifier}", e)
                // Bei Fehler: null zurückgeben. Der Aufrufer kann entscheiden, ob er einen älteren Wert anzeigt.
                onResult(null)
            }
        }, 0, TimeUnit.SECONDS)
    }

    /**
     * Liest Statistiken synchron aus dem Cache, ohne einen Netzwerkabruf auszulösen.
     * Gibt null zurück, wenn keine gültigen Statistiken im Cache sind oder der Cache korrupt ist.
     */
    fun getCachedStatistics(weatherLocation: WeatherLocation): Statistics? {
        val locationKey = weatherLocation.key.id
        val state = stateCache.read(locationKey)
        return if (state.statistics.isNotEmpty()) {
            try {
                JsonTranslator.toSingleStatistics(state.statistics)
            } catch (e: Exception) {
                Log.e(TAG, "Error deserializing cached statistics for $locationKey on getCachedStatistics", e)
                null // Cache ist korrupt
            }
        } else {
            null
        }
    }

    private fun updateCache(locationKey: String, statistics: Statistics) {
        val newState = State(
            id = locationKey,
            age = Date(),
            statistics = JsonTranslator.toString(statistics)
        )
        stateCache.save(newState)
    }

    /**
     * Löscht zwischengespeicherte Statistiken und deren Alter für einen Standort,
     * indem das Alter auf null und die Statistikdaten auf einen leeren String gesetzt werden.
     */
    fun clearCachedStatistics(weatherLocation: WeatherLocation) {
        val locationKey = weatherLocation.key.id
        val currentState = stateCache.read(locationKey)
        val clearedState = currentState.copy(
            age = null, 
            statistics = "" 
        )
        stateCache.save(clearedState)
    }

    // Die alten, LocationView-basierten Methoden wurden entfernt.
}
