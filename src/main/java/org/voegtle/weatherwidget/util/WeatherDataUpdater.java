package org.voegtle.weatherwidget.util;

import org.voegtle.weatherwidget.WeatherActivity;
import org.voegtle.weatherwidget.preferences.WeatherActivityConfiguration;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class WeatherDataUpdater {
  private ScheduledFuture<?> backgroundProcess;

  private WeatherActivity activity;
  private WeatherActivityConfiguration configuration;

  public WeatherDataUpdater(WeatherActivity activity, WeatherActivityConfiguration configuration) {
    this.activity = activity;
    this.configuration = configuration;
  }

  public void stopWeatherScheduler() {
    if (backgroundProcess != null) {
      backgroundProcess.cancel(true);
      backgroundProcess = null;
    }
  }

  public void startWeatherScheduler(int intervall) {
    stopWeatherScheduler();

    final Runnable updater = new Runnable() {
      @Override
      public void run() {
        new ActivityUpdateTask(activity, configuration, false).execute();
      }
    };
    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    backgroundProcess = scheduler.scheduleAtFixedRate(updater, intervall, intervall, TimeUnit.SECONDS);
  }

  public void updateWeatherOnce(final boolean showToast) {
    new ActivityUpdateTask(activity, configuration, showToast).execute();
  }

}
