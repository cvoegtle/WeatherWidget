package org.voegtle.weatherwidget.util;

import android.app.Activity;
import org.voegtle.weatherwidget.data.Statistics;
import org.voegtle.weatherwidget.location.LocationView;
import org.voegtle.weatherwidget.location.WeatherLocation;
import org.voegtle.weatherwidget.state.State;
import org.voegtle.weatherwidget.state.StateCache;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class StatisticsUpdater {
  private StateCache stateCache;

  private WeatherDataFetcher weatherDataFetcher;

  public StatisticsUpdater(Activity activity) {
    this.stateCache = new StateCache(activity);

    this.weatherDataFetcher = new WeatherDataFetcher(ContextUtil.getBuildNumber(activity));

  }

  public void setupStatistics(final LocationView locationView) {
    State state = stateCache.read(locationView.getId());
    locationView.setExpanded(state.isExpanded());
  }

  public void updateStatistics(LocationView locationView, WeatherLocation location) {
    HashMap<LocationView, WeatherLocation> updateCandidates = new HashMap<>();
    updateCandidates.put(locationView, location);
    updateStatistics(updateCandidates, true);
  }

  public void updateStatistics(final HashMap<LocationView, WeatherLocation> updateCandidates, final boolean forceUpdate) {
    updateCachedLocations(updateCandidates);

    final Runnable updater = new Runnable() {
      @Override
      public void run() {
        ArrayList<String> outdatedLocations = lookupOutdatedLocations(updateCandidates, forceUpdate);

        HashMap<String, Statistics> statistics = weatherDataFetcher.fetchStatisticsFromUrl(outdatedLocations);
        updateLocations(updateCandidates, statistics);
      }
    };
    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    scheduler.schedule(updater, 0, TimeUnit.SECONDS);
  }


  private void updateLocations(HashMap<LocationView, WeatherLocation> updateCandidates, HashMap<String, Statistics> statistics) {
    for (LocationView locationView : updateCandidates.keySet()) {
      WeatherLocation location = updateCandidates.get(locationView);
      Statistics stats = statistics.get(location.getIdentifier());
      if (stats != null) {
        updateLocation(locationView, stats);
      }

    }
  }

  private ArrayList<String> lookupOutdatedLocations(HashMap<LocationView, WeatherLocation> updateCandidates, boolean forceUpdate) {
    ArrayList<String> outdatedLocations = new ArrayList<>();
    for (LocationView locationView : updateCandidates.keySet()) {
      State state = stateCache.read(locationView.getId());
      if (state.outdated() || forceUpdate) {
        WeatherLocation outdatedLocation = updateCandidates.get(locationView);
        outdatedLocations.add(outdatedLocation.getIdentifier());
      }

    }
    return outdatedLocations;
  }

  private void updateCachedLocations(HashMap<LocationView, WeatherLocation> updateCandidates) {
    for (LocationView locationView : updateCandidates.keySet()) {
      State state = stateCache.read(locationView.getId());
      if (!state.outdated()) {
        updateView(locationView, JsonTranslator.toSingleStatistics(state.getStatistics()));
      }
    }
  }

  private void updateLocation(final LocationView locationView, final Statistics statistics) {
    locationView.post(new Runnable() {
      @Override
      public void run() {
        updateView(locationView, statistics);
        updateCache(locationView, statistics);
      }
    });
  }

  private void updateCache(LocationView locationView, Statistics statistics) {
    State state = new State(locationView.getId());
    state.setAge(new Date());
    state.setExpanded(locationView.isExpanded());
    state.setStatistics(JsonTranslator.toString(statistics));
    stateCache.save(state);
  }

  private void updateView(final LocationView locationView, final Statistics moreData) {
    locationView.setMoreData(moreData);
  }

  public void clearState(LocationView locationView) {
    State state = stateCache.read(locationView.getId());
    state.setExpanded(false);
    state.setStatistics("");
    state.setAge(DateUtil.getYesterday());
    stateCache.save(state);
  }

}
