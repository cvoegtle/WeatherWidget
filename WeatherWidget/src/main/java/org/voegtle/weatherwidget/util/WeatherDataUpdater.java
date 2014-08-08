package org.voegtle.weatherwidget.util;

import android.content.res.Resources;
import android.util.Log;
import org.voegtle.weatherwidget.R;
import org.voegtle.weatherwidget.WeatherActivity;
import org.voegtle.weatherwidget.data.WeatherData;
import org.voegtle.weatherwidget.location.LocationView;
import org.voegtle.weatherwidget.location.WeatherLocation;
import org.voegtle.weatherwidget.notification.NotificationSystemManager;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class WeatherDataUpdater {
  private WeatherActivity activity;
  private List<WeatherLocation> locations;
  private final Resources res;
  private final WeatherDataFetcher weatherDataFetcher;
  private DecimalFormat numberFormat;
  private ScheduledFuture<?> backgroundProcess;

  public WeatherDataUpdater(WeatherActivity activity, List<WeatherLocation> locations) {
    this.activity = activity;
    this.locations = locations;
    this.res = activity.getResources();
    this.weatherDataFetcher = new WeatherDataFetcher();

    this.numberFormat = (DecimalFormat) NumberFormat.getNumberInstance(Locale.GERMANY);
    this.numberFormat.applyPattern("###.#");
  }

  public void stopWeatherScheduler() {
    if (backgroundProcess != null) {
      backgroundProcess.cancel(true);
    }
  }

  public void startWeatherScheduler(int intervall) {
    stopWeatherScheduler();

    final Runnable updater = new Runnable() {
      @Override
      public void run() {
        updateWeatherLocations(false);
      }
    };
    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    backgroundProcess = scheduler.scheduleAtFixedRate(updater, intervall, intervall, TimeUnit.SECONDS);
  }

  public void updateWeatherOnce(final boolean showToast) {
    final Runnable updater = new Runnable() {
      @Override
      public void run() {
        updateWeatherLocations(showToast);
      }
    };
    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    scheduler.schedule(updater, 0, TimeUnit.SECONDS);
  }

  private boolean updateInProgress;

  private void updateWeatherLocations(boolean showToast) {
    if (updateInProgress) {
      return;
    }

    try {
      updateInProgress = true;
      HashMap<String, WeatherData> data = weatherDataFetcher.fetchAllWeatherDataFromServer();
      for (WeatherLocation location : locations) {
        updateWeatherLocation(location.getWeatherViewId(),
            location.getName(),
            data.get(location.getKey().toString()));
      }

      new UserFeedback(activity).showMessage(R.string.message_data_updated, showToast);

      NotificationSystemManager notificationManager = new NotificationSystemManager(activity);
      notificationManager.checkDataForAlert(data);
    } catch (Throwable th) {
      new UserFeedback(activity).showMessage(R.string.message_data_update_failed, showToast);
      Log.e(WeatherDataUpdater.class.toString(), "Failed to update View");
    } finally {
      updateInProgress = false;
    }
  }


  private void updateWeatherLocation(int locationId, String locationName, WeatherData data) {
    final LocationView contentView = (LocationView) activity.findViewById(locationId);

    final int color = ColorUtil.byAge(data.getTimestamp());
    final String caption = getCaption(locationName, data);
    final String text = formatWeatherData(data);

    updateView(contentView, caption, text, color);
  }

  private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

  private String getCaption(String locationName, WeatherData data) {
    return locationName + " - " + sdf.format(data.getTimestamp());
  }


  private String formatWeatherData(WeatherData data) {
    StringBuilder builder = new StringBuilder();
    builder.append(res.getString(R.string.temperature)).append(" ");
    builder.append(numberFormat.format(data.getTemperature())).append("°C").append("\n");
    builder.append(res.getString(R.string.humidity)).append(" ").append(numberFormat.format(data.getHumidity())).append("%");
    if (data.getRain() != null) {
      builder.append("\n");
      builder.append(res.getString(R.string.rain_last_hour)).append(" ").append(numberFormat.format(data.getRain())).append(res.getString(R.string.liter));
    }
    if (data.getRainToday() != null) {
      builder.append("\n");
      builder.append(res.getString(R.string.rain_today)).append(" ").append(numberFormat.format(data.getRainToday())).append(res.getString(R.string.liter));
    }
    return builder.toString();
  }

  private void updateView(final LocationView view, final String caption, final String text, final int color) {
    view.post(new Runnable() {
      @Override
      public void run() {
        view.setCaption(caption);
        view.setData(text);
        view.setTextColor(color);
      }
    });
  }

}
