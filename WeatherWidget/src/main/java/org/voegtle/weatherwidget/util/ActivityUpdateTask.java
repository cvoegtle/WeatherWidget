package org.voegtle.weatherwidget.util;

import android.content.res.Resources;
import android.os.AsyncTask;
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

public class ActivityUpdateTask extends AsyncTask<Void, Void, HashMap<String, WeatherData>> {
  private WeatherActivity activity;
  private List<WeatherLocation> locations;
  private final Resources res;
  private DecimalFormat numberFormat;

  private final WeatherDataFetcher weatherDataFetcher;

  private boolean showToast;

  public ActivityUpdateTask(WeatherActivity activity, List<WeatherLocation> locations, boolean showToast) {
    this.activity = activity;
    this.locations = locations;
    this.showToast = showToast;
    this.res = activity.getResources();
    this.weatherDataFetcher = new WeatherDataFetcher();

    this.numberFormat = (DecimalFormat) NumberFormat.getNumberInstance(Locale.GERMANY);
    this.numberFormat.applyPattern("###.#");
  }

  @Override
  protected HashMap<String, WeatherData> doInBackground(Void... voids) {
    return weatherDataFetcher.fetchAllWeatherDataFromServer();
  }

  @Override
  protected void onPostExecute(HashMap<String, WeatherData> data) {
    try {
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
    view.setCaption(caption);
    view.setData(text);
    view.setTextColor(color);
  }
}