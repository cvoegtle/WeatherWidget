package org.voegtle.weatherwidget.location;

import android.content.Context;
import android.widget.LinearLayout;
import org.voegtle.weatherwidget.data.WeatherData;
import org.voegtle.weatherwidget.preferences.ApplicationSettings;

import java.util.*;

public class LocationContainer {

  private final LocationOrderStore locationOrderStore;
  private final LinearLayout container;
  private final ApplicationSettings configuration;
  private List<WeatherLocation> locations;

  public LocationContainer(Context context, LinearLayout container, ApplicationSettings configuration) {
    this.container = container;
    this.locations = configuration.getLocations();
    this.configuration = configuration;
    this.locationOrderStore = new LocationOrderStore(context);
  }

  public void updateLocationOrder(HashMap<LocationIdentifier, WeatherData> weatherData) {
    ArrayList<WeatherData> sortedWeatherData = sort(weatherData);

    for (int i = 0; i < sortedWeatherData.size(); i++) {
      WeatherData data = sortedWeatherData.get(i);
      LocationView view = (LocationView) container.getChildAt(i);
      if (!belongTogether(data, view)) {
        view = moveViewToPosition(i, data);
      }
      manageViewPosition(view, i);
    }

  }

  private boolean belongTogether(WeatherData data, LocationView view) {
    WeatherLocation location = findLocation(data);
    return location.getWeatherViewId() == view.getId();
  }

  private LocationView moveViewToPosition(int i, WeatherData data) {
    LocationView view = findLocationView(data);
    container.removeView(view);
    container.addView(view, i);
    return view;
  }


  private WeatherLocation findLocation(WeatherData data) {
    for (WeatherLocation location : locations) {
      if (location.getKey().equals(data.getLocation())) {
        return location;
      }
    }
    return null;
  }

  private LocationView findLocationView(WeatherData data) {
    WeatherLocation location = findLocation(data);
    return (LocationView) (container.findViewById(location.getWeatherViewId()));
  }

  private void manageViewPosition(LocationView view, int position) {
    int oldPosition = locationOrderStore.readIndexOf(view.getId());
    locationOrderStore.writeIndexOf(view.getId(), position);
    view.highlight(position < oldPosition);
  }


  private ArrayList<WeatherData> sort(HashMap<LocationIdentifier, WeatherData> weatherData) {
    ArrayList<WeatherData> sortedWeatherData = new ArrayList<>();
    for (WeatherData data : weatherData.values()) {
      sortedWeatherData.add(data);
    }
    Comparator<WeatherData> comparator = LocationComparatorFactory.createComparator(configuration.getOrderCriteria());
    Collections.sort(sortedWeatherData, comparator);
    return sortedWeatherData;
  }
}
