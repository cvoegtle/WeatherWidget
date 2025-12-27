package org.voegtle.weatherwidget.util

import android.content.Context
import androidx.work.WorkerParameters
import org.voegtle.weatherwidget.data.Statistics
import org.voegtle.weatherwidget.cache.State
import org.voegtle.weatherwidget.cache.StateCache
import org.voegtle.weatherwidget.location.LocationIdentifier
import java.util.Date

class StatisticUpdateWorker(appContext: Context, workerParams: WorkerParameters) : UpdateWorker(appContext, workerParams) {
    companion object {
        const val STATISTIC_DATA = "STATISTIC_DATA"
    }
    private val stateCache = StateCache(appContext)

    override fun doWork(): Result {
        val updateCandidates = configuration.locations.map { it.key }.toList()
        val outdatedLocations = lookupOutdatedLocations(updateCandidates)

        val statistics = weatherDataFetcher.fetchStatisticsFromUrl(outdatedLocations)
        updateStateCache(statistics)
        return Result.success()
    }

    private fun lookupOutdatedLocations(updateCandidates: Collection<LocationIdentifier>): ArrayList<LocationIdentifier> {
        val outdatedLocations = ArrayList<LocationIdentifier>()
        for (locationIdentifier in updateCandidates) {
            val state = stateCache.read(locationIdentifier)
            if (state.outdated() && state.isExpanded) {
                outdatedLocations.add(locationIdentifier)
            }

        }
        return outdatedLocations
    }

    private fun updateStateCache(statistics: Collection<Statistics>) {
        for (statistic in statistics) {
            val state = State(statistic.id, true, Date(), JsonTranslator.toString(statistic))
            stateCache.save(state)
        }
    }

}