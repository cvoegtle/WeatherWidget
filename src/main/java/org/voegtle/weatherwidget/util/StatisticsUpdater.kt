package org.voegtle.weatherwidget.util

import android.app.Activity
import org.voegtle.weatherwidget.data.Statistics
import org.voegtle.weatherwidget.location.LocationView
import org.voegtle.weatherwidget.location.WeatherLocation
import org.voegtle.weatherwidget.state.State
import org.voegtle.weatherwidget.state.StateCache

import java.util.ArrayList
import java.util.Date
import java.util.HashMap
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class StatisticsUpdater(activity: Activity) {
  private val stateCache: StateCache

  private val weatherDataFetcher: WeatherDataFetcher

  init {
    this.stateCache = StateCache(activity)

    this.weatherDataFetcher = WeatherDataFetcher(ContextUtil.getBuildNumber(activity))

  }

  fun setupStatistics(locationView: LocationView) {
    val state = stateCache.read(locationView.id)
    locationView.isExpanded = state.isExpanded
  }

  fun updateStatistics(locationView: LocationView, location: WeatherLocation) {
    val updateCandidates = HashMap<LocationView, WeatherLocation>()
    updateCandidates.put(locationView, location)
    updateStatistics(updateCandidates, true)
  }

  fun updateStatistics(updateCandidates: HashMap<LocationView, WeatherLocation>, forceUpdate: Boolean) {
    updateCachedLocations(updateCandidates)

    val updater = Runnable {
      val outdatedLocations = lookupOutdatedLocations(updateCandidates, forceUpdate)

      val statistics = weatherDataFetcher.fetchStatisticsFromUrl(outdatedLocations)
      updateLocations(updateCandidates, statistics)
    }
    val scheduler = Executors.newScheduledThreadPool(1)
    scheduler.schedule(updater, 0, TimeUnit.SECONDS)
  }


  private fun updateLocations(updateCandidates: HashMap<LocationView, WeatherLocation>, statistics: HashMap<String, Statistics>) {
    for (locationView in updateCandidates.keys) {
      val location: WeatherLocation? = updateCandidates[locationView]
      val stats = statistics[location?.identifier]
      if (stats != null) {
        updateLocation(locationView, stats)
      }

    }
  }

  private fun lookupOutdatedLocations(updateCandidates: HashMap<LocationView, WeatherLocation>, forceUpdate: Boolean): ArrayList<String> {
    val outdatedLocations = ArrayList<String>()
    for (locationView in updateCandidates.keys) {
      val state = stateCache.read(locationView.id)
      if (state.outdated() || forceUpdate) {
        val outdatedLocation: WeatherLocation? = updateCandidates[locationView]
        outdatedLocations.add(outdatedLocation!!.identifier!!)
      }

    }
    return outdatedLocations
  }

  private fun updateCachedLocations(updateCandidates: HashMap<LocationView, WeatherLocation>) {
    for (locationView in updateCandidates.keys) {
      val state = stateCache.read(locationView.id)
      if (!state.outdated()) {
        updateView(locationView, JsonTranslator.toSingleStatistics(state.statistics))
      }
    }
  }

  private fun updateLocation(locationView: LocationView, statistics: Statistics) {
    locationView.post {
      updateView(locationView, statistics)
      updateCache(locationView, statistics)
    }
  }

  private fun updateCache(locationView: LocationView, statistics: Statistics) {
    val state = State(locationView.id)
    state.age = Date()
    state.isExpanded = locationView.isExpanded
    state.statistics = JsonTranslator.toString(statistics)
    stateCache.save(state)
  }

  private fun updateView(locationView: LocationView, moreData: Statistics) {
    locationView.setMoreData(moreData)
  }

  fun clearState(locationView: LocationView) {
    val state = stateCache.read(locationView.id)
    state.isExpanded = false
    state.statistics = ""
    state.age = DateUtil.yesterday
    stateCache.save(state)
  }

}
