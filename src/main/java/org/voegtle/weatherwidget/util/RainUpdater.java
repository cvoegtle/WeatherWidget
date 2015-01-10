package org.voegtle.weatherwidget.util;

import android.app.Activity;
import android.net.Uri;
import org.voegtle.weatherwidget.data.RainData;
import org.voegtle.weatherwidget.location.LocationView;
import org.voegtle.weatherwidget.state.State;
import org.voegtle.weatherwidget.state.StateCache;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class RainUpdater {
  private StateCache stateCache;

  private WeatherDataFetcher weatherDataFetcher;
  private DataFormatter formatter;

  public RainUpdater(Activity activity) {
    this.formatter = new DataFormatter(activity.getResources());
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
        updateView(locationView, state.getRainData());
      }
    }
  }

  public void updateRain(final LocationView locationView, final Uri uri) {
    final Runnable updater = new Runnable() {
      @Override
      public void run() {
        State state = stateCache.read(locationView.getId());
        if (state.outdated()) {
          RainData rainData = weatherDataFetcher.fetchRainDataFromUrl(uri);
          updateLocation(locationView, rainData);
        } else {
          updateView(locationView, state.getRainData());
        }
      }
    };
    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    scheduler.schedule(updater, 0, TimeUnit.SECONDS);

  }

  private void updateLocation(final LocationView locationView, RainData rainData) {
    String rainString = formatter.formatRainData(rainData);
    updateView(locationView, rainString);
    updateCache(locationView, rainString);
  }

  private void updateCache(LocationView locationView, String rainData) {
    State state = new State(locationView.getId());
    state.setAge(new Date());
    state.setExpanded(locationView.isExpanded());
    state.setRainData(rainData);
    stateCache.save(state);
  }

  private void updateView(final LocationView locationView, final String moreData) {
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
    state.setRainData("");
    state.setAge(DateUtil.getYesterday());
    stateCache.save(state);
  }
}
