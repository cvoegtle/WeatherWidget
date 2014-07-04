package org.voegtle.weatherwidget.util;

import android.content.res.Resources;
import android.util.Log;
import android.widget.TextView;
import org.voegtle.weatherwidget.R;
import org.voegtle.weatherwidget.WeatherActivity;
import org.voegtle.weatherwidget.data.WeatherData;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class WeatherDataUpdater {
  private WeatherActivity activity;
  private final Resources res;
  private final WeatherDataFetcher weatherDataFetcher;
  private DecimalFormat numberFormat;

  public WeatherDataUpdater(WeatherActivity activity) {
    this.activity = activity;
    this.res = activity.getResources();
    this.weatherDataFetcher = new WeatherDataFetcher();
    numberFormat = (DecimalFormat) NumberFormat.getNumberInstance(Locale.GERMANY);
    numberFormat.applyPattern("###.#");
  }

  public void startWeatherScheduler() {
    final Runnable updater = new Runnable() {
      @Override
      public void run() {
        try {
          HashMap<String, WeatherData> data = weatherDataFetcher.fetchAllWeatherDataFromServer();
          updatePaderbornWeather(data.get("Paderborn"));
          updateBonnWeather(data.get("Bonn"));
          updateFreiburgWeather(data.get("Freiburg"));
        } catch (Throwable th) {
          Log.e(WeatherDataUpdater.class.toString(), "Failed to update View");
        }
      }
    };
    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    scheduler.scheduleAtFixedRate(updater, 0, 3 * 60, TimeUnit.SECONDS);

  }

  private void updatePaderbornWeather(WeatherData data) {
    updateWeatherLocation(R.id.caption_paderborn, R.id.weather_paderborn, R.string.city_paderborn_full, data);
  }

  private void updateFreiburgWeather(WeatherData data) {
    updateWeatherLocation(R.id.caption_freiburg, R.id.weather_freiburg, R.string.city_freiburg_full, data);
  }

  private void updateBonnWeather(WeatherData data) {
    updateWeatherLocation(R.id.caption_bonn, R.id.weather_bonn, R.string.city_bonn_full, data);
  }

  private void updateWeatherLocation(int captionId, int contentId, int locationName, WeatherData data) {
    final TextView captionView = (TextView) activity.findViewById(captionId);
    final String caption = getCaption(locationName, data);
    final int color = ColorUtil.byAge(data.getTimestamp());
    updateView(captionView, caption, color);


    final TextView contentView = (TextView) activity.findViewById(contentId);
    final String text = formatWeatherData(data);
    updateView(contentView, text, color);
  }

  private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

  private String getCaption(int resourceKey, WeatherData data) {
    return res.getString(resourceKey) + " - " + sdf.format(data.getTimestamp());
  }


  private String formatWeatherData(WeatherData data) {
    StringBuilder builder = new StringBuilder();
    builder.append(res.getString(R.string.temperature)).append(" ");
    builder.append(numberFormat.format(data.getTemperature())).append(res.getString(R.string.degree_centigrade)).append("\n");
    builder.append(res.getString(R.string.humidity)).append(" ").append(numberFormat.format(data.getHumidity())).append("%\n");
    if (data.getRain() != null) {
      builder.append(res.getString(R.string.rain_last_hour)).append(" ").append(numberFormat.format(data.getRain())).append(res.getString(R.string.liter)).append("\n");
    }
    if (data.getRainToday() != null) {
      builder.append(res.getString(R.string.rain_today)).append(" ").append(numberFormat.format(data.getRainToday())).append(res.getString(R.string.liter)).append("\n");
    }
    return builder.toString();
  }

  private void updateView(final TextView view, final String text, final int color) {
    view.post(new Runnable() {
      @Override
      public void run() {
        view.setText(text);
        view.setTextColor(color);
      }
    });
  }

}
