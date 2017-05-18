package org.voegtle.weatherwidget.util;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.LinearLayout;
import org.voegtle.weatherwidget.R;
import org.voegtle.weatherwidget.WeatherActivity;
import org.voegtle.weatherwidget.data.WeatherData;
import org.voegtle.weatherwidget.location.LocationContainer;
import org.voegtle.weatherwidget.location.LocationIdentifier;
import org.voegtle.weatherwidget.location.LocationView;
import org.voegtle.weatherwidget.location.WeatherLocation;
import org.voegtle.weatherwidget.notification.NotificationSystemManager;
import org.voegtle.weatherwidget.preferences.ApplicationSettings;
import org.voegtle.weatherwidget.widget.ScreenPainterFactory;
import org.voegtle.weatherwidget.widget.WidgetScreenPainter;

import java.util.ArrayList;
import java.util.HashMap;

public class ActivityUpdateTask extends AsyncTask<Void, Void, HashMap<LocationIdentifier, WeatherData>> {
  private final WeatherDataFetcher weatherDataFetcher;
  private WeatherActivity activity;
  private boolean showToast;
  private ApplicationSettings configuration;

  ActivityUpdateTask(WeatherActivity activity, ApplicationSettings configuration, boolean showToast) {
    this.configuration = configuration;
    this.activity = activity;

    this.showToast = showToast;
    this.weatherDataFetcher = new WeatherDataFetcher(ContextUtil.getBuildNumber(activity));
  }


  @Override
  protected HashMap<LocationIdentifier, WeatherData> doInBackground(Void... voids) {
    return weatherDataFetcher.fetchAllWeatherDataFromServer(configuration.getLocations(), configuration.getSecret());
  }

  @Override
  protected void onPostExecute(HashMap<LocationIdentifier, WeatherData> data) {
    try {
      updateViewData(data);
      sortViews(data);
      updateWidgets(data);

      new UserFeedback(activity).showMessage(R.string.message_data_updated, showToast);

      NotificationSystemManager notificationManager = new NotificationSystemManager(activity, configuration);
      notificationManager.checkDataForAlert(data);
    } catch (Throwable th) {
      new UserFeedback(activity).showMessage(R.string.message_data_update_failed, true);
      Log.e(ActivityUpdateTask.class.toString(), "Failed to update View", th);
    }
  }

  private void updateWidgets(HashMap<LocationIdentifier, WeatherData> data) {
    ScreenPainterFactory factory = new ScreenPainterFactory(activity, configuration);
    ArrayList<WidgetScreenPainter> screenPainters = factory.createScreenPainters();
    for (WidgetScreenPainter screenPainter : screenPainters) {
      screenPainter.updateWidgetData(data);
      screenPainter.showDataIsValid();
    }
  }

  private void updateViewData(HashMap<LocationIdentifier, WeatherData> data) {
    for (WeatherLocation location : configuration.getLocations()) {
      WeatherData locationData = data.get(location.getKey());
      if (locationData != null) {
        updateWeatherLocation(location.getWeatherViewId(),
            location.getName(), locationData);
      }
    }
  }

  private void updateWeatherLocation(int locationId, String locationName, WeatherData data) {
    final LocationView contentView = (LocationView) activity.findViewById(locationId);

    final int color = ColorUtil.byAge(configuration.getColorScheme(), data.getTimestamp());
    final String caption = getCaption(locationName, data);

    updateView(contentView, caption, data, color);
  }

  private String getCaption(String locationName, WeatherData data) {
    return locationName + " - " + data.getLocaltime();
  }

  private void updateView(final LocationView view, final String caption, final WeatherData data, final int color) {
    view.setCaption(caption);
    view.setData(data);
    view.setTextColor(color);
  }

  private void sortViews(HashMap<LocationIdentifier, WeatherData> data) {
    LinearLayout container = (LinearLayout) (activity.findViewById(R.id.location_container));
    LocationContainer locationContainer = new LocationContainer(activity.getApplicationContext(), container, configuration);
    locationContainer.updateLocationOrder(data);
  }

}
