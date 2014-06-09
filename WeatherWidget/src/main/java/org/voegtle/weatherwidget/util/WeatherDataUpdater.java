package org.voegtle.weatherwidget.util;

import android.app.Activity;
import android.content.res.Resources;
import android.util.Log;
import android.widget.TextView;
import org.voegtle.weatherwidget.R;
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
  private Activity activity;
  private final Resources res;
  private final WeatherDataFetcher weatherDataFetcher;
  private DecimalFormat numberFormat;

  public WeatherDataUpdater(Activity activity, Resources res) {
    this.activity = activity;
    this.res = res;
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

    TextView pbCaption = (TextView) activity.findViewById(R.id.caption_paderborn);
    final String caption = getCaption(R.string.city_paderborn_full, data);
    updateView(pbCaption, caption);

    TextView pbView = (TextView) activity.findViewById(R.id.weather_paderborn);
    String text = formatWeatherData(data);
    updateView(pbView, text);
  }

  private void updateFreiburgWeather(WeatherData data) {
    TextView frCaption = (TextView) activity.findViewById(R.id.caption_freiburg);
    final String caption = getCaption(R.string.city_freiburg_full, data);
    updateView(frCaption, caption);


    TextView frView = (TextView) activity.findViewById(R.id.weather_freiburg);
    final String text = formatWeatherData(data);
    updateView(frView, text);
  }

  private void updateBonnWeather(WeatherData data) {
    TextView frCaption = (TextView) activity.findViewById(R.id.caption_bonn);
    final String caption = getCaption(R.string.city_bonn_full, data);
    updateView(frCaption, caption);


    TextView frView = (TextView) activity.findViewById(R.id.weather_bonn);
    final String text = formatWeatherData(data);
    updateView(frView, text);
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

  private void updateView(final TextView view, final String text) {
    view.post(new Runnable() {
      @Override
      public void run() {
        view.setText(text);
      }
    });
  }

}
