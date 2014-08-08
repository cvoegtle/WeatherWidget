package org.voegtle.weatherwidget.util;

import android.app.Activity;
import android.content.res.Resources;
import android.net.Uri;
import org.voegtle.weatherwidget.R;
import org.voegtle.weatherwidget.data.RainData;
import org.voegtle.weatherwidget.location.LocationView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class RainUpdater {

  private Resources res;

  private WeatherDataFetcher weatherDataFetcher;
  private DecimalFormat numberFormat;

  public RainUpdater(Activity activity) {
    this.res = activity.getResources();
    this.weatherDataFetcher = new WeatherDataFetcher();
    this.numberFormat = (DecimalFormat) NumberFormat.getNumberInstance(Locale.GERMANY);
    this.numberFormat.applyPattern("###.#");

  }

  public void updateRain(final LocationView locationView, final Uri uri) {
    final Runnable updater = new Runnable() {
      @Override
      public void run() {
        RainData rainData = weatherDataFetcher.fetchRainDataFromUrl(uri);
        updateLocation(locationView, rainData);
      }
    };
    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    scheduler.schedule(updater, 0, TimeUnit.SECONDS);

  }

  private void updateLocation(final LocationView locationView, RainData rainData) {
    final StringBuilder builder = new StringBuilder();

    builder.append(buildRainString(res.getString(R.string.rain_yesterday), rainData.getRainYeasterday())).append("\n");
    builder.append(buildRainString(res.getString(R.string.rain_last_week), rainData.getRainLastWeek())).append("\n");
    builder.append(buildRainString(res.getString(R.string.rain_30_days), rainData.getRain30Days()));

    locationView.post(new Runnable() {
      @Override
      public void run() {
        locationView.setMoreData(builder.toString());
      }
    });
  }

  private String buildRainString(String text, Float value) {
    StringBuilder builder = new StringBuilder();

    builder.append(text);
    builder.append(" ");
    if (value != null) {
      builder.append(numberFormat.format(value));
      builder.append(res.getString(R.string.liter));
    }
    return builder.toString();
  }
}
