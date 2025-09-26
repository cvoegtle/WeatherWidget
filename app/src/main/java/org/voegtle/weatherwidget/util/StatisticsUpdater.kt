package org.voegtle.weatherwidget.util

import android.app.Activity
import org.voegtle.weatherwidget.data.Statistics
import org.voegtle.weatherwidget.location.LocationIdentifier
import org.voegtle.weatherwidget.location.WeatherLocation
import org.voegtle.weatherwidget.state.State
import org.voegtle.weatherwidget.state.StateCache
import java.util.Date
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class StatisticsUpdater(activity: Activity) {
  private val stateCache = StateCache(activity)
  private val weatherDataFetcher = WeatherDataFetcher(ContextUtil.getBuildNumber(activity))

  fun updateStatistics(updateCandidates: Collection<LocationIdentifier>, forceUpdate: Boolean) {
    val updater = Runnable {
      val outdatedLocations = lookupOutdatedLocations(updateCandidates, forceUpdate)

      val statistics = weatherDataFetcher.fetchStatisticsFromUrl(outdatedLocations)
      updateLocations(statistics)
    }
    val scheduler = Executors.newScheduledThreadPool(1)
    scheduler.schedule(updater, 0, TimeUnit.SECONDS)
  }


  private fun updateLocations(statistics: Collection<Statistics>) {
    for (statistic in statistics) {
      val state = State(statistic.id, true, Date(), JsonTranslator.toString(statistic))
      stateCache.save(state)
    }
  }

  private fun lookupOutdatedLocations(updateCandidates: Collection<LocationIdentifier>, forceUpdate: Boolean): ArrayList<LocationIdentifier> {
    val outdatedLocations = ArrayList<LocationIdentifier>()
    for (locationIdentifier in updateCandidates) {
      val state = stateCache.read(locationIdentifier)
      if (state.outdated() || forceUpdate) {
        outdatedLocations.add(locationIdentifier)
      }

    }
    return outdatedLocations
  }
}
