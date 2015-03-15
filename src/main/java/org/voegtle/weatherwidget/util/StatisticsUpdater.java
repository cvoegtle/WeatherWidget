package org.voegtle.weatherwidget.util;

import android.app.Activity;
import android.net.Uri;
import org.voegtle.weatherwidget.data.Statistics;
import org.voegtle.weatherwidget.location.LocationView;
import org.voegtle.weatherwidget.state.State;
import org.voegtle.weatherwidget.state.StateCache;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class StatisticsUpdater {
  private StateCache stateCache;

  private WeatherDataFetcher weatherDataFetcher;

  public StatisticsUpdater(Activity activity) {
    this.stateCache = new StateCache(activity);

    this.weatherDataFetcher = new WeatherDataFetcher();

  }

  public void setupRain(final LocationView locationView, final Uri uri) {
    State state = stateCache.read(locationView.getId());
    locationView.setExpanded(state.isExpanded());
    if (state.isExpanded()) {
      if (state.outdated()) {
        updateRain(locationView, uri);
      } else {
        updateView(locationView, JsonTranslater.toStatistics(state.getStatistics()));
      }
    }
  }

  public void updateRain(final LocationView locationView, final Uri uri) {
    final Runnable updater = new Runnable() {
      @Override
      public void run() {
        State state = stateCache.read(locationView.getId());
        if (state.outdated()) {
          Statistics statistics = weatherDataFetcher.fetchStatisticsFromUrl(uri);
          updateLocation(locationView, statistics);
        } else {
          updateView(locationView, JsonTranslater.toStatistics(state.getStatistics()));
        }
      }
    };
    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    scheduler.schedule(updater, 0, TimeUnit.SECONDS);

  }

  private void updateLocation(final LocationView locationView, Statistics statistics) {
    updateView(locationView, statistics);
    updateCache(locationView, statistics);
  }

  private void updateCache(LocationView locationView, Statistics statistics) {
    State state = new State(locationView.getId());
    state.setAge(new Date());
    state.setExpanded(locationView.isExpanded());
    state.setStatistics(JsonTranslater.toString(statistics));
    stateCache.save(state);
  }

  private void updateView(final LocationView locationView, final Statistics moreData) {
    locationView.post(new Runnable() {
      @Override
      public void run() {
        locationView.setMoreData(moreData);
      }
    });
  }

  public void clearState(LocationView locationView) {
    State state = stateCache.read(locationView.getId());
    state.setExpanded(false);
    state.setStatistics("");
    state.setAge(DateUtil.getYesterday());
    stateCache.save(state);
  }
}
