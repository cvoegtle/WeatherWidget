package org.voegtle.weatherwidget.location;

import android.content.res.Resources;
import android.net.Uri;
import org.voegtle.weatherwidget.R;

import java.util.ArrayList;
import java.util.List;

public class LocationFactory {
  static public List<WeatherLocation> buildWeatherLocations(Resources res) {
    ArrayList<WeatherLocation> locations = new ArrayList<WeatherLocation>();

    WeatherLocation paderborn = new WeatherLocation(LocationIdentifier.Paderborn);
    paderborn.setName(res.getString(R.string.city_paderborn_full));
    paderborn.setShortName(res.getString(R.string.city_paderborn));
    paderborn.setForecastUrl(Uri.parse("http://wetterstationen.meteomedia.de/?station=104300&wahl=vorhersage"));
    paderborn.setWeatherViewId(R.id.weather_paderborn);
    paderborn.setForecastButtonId(R.id.button_forecast_paderborn);
    paderborn.setPrefShowInApp("app_show_paderborn");
    paderborn.setPrefShowInWidget("widget_show_paderborn");
    paderborn.setPrefAlert("alert_paderborn");
    locations.add(paderborn);

    WeatherLocation bonn = new WeatherLocation(LocationIdentifier.Bonn);
    bonn.setName(res.getString(R.string.city_bonn_full));
    bonn.setShortName(res.getString(R.string.city_bonn));
    bonn.setForecastUrl(Uri.parse("http://wetterstationen.meteomedia.de/?station=105170&wahl=vorhersage"));
    bonn.setWeatherViewId(R.id.weather_bonn);
    bonn.setForecastButtonId(R.id.button_forecast_bonn);
    bonn.setPrefShowInApp("app_show_bonn");
    bonn.setPrefShowInWidget("widget_show_bonn");
    bonn.setPrefAlert("alert_bonn");
    locations.add(bonn);

    WeatherLocation freiburg = new WeatherLocation(LocationIdentifier.Freiburg);
    freiburg.setName(res.getString(R.string.city_freiburg_full));
    freiburg.setShortName(res.getString(R.string.city_freiburg));
    freiburg.setForecastUrl(Uri.parse("http://wetterstationen.meteomedia.de/?station=108030&wahl=vorhersage"));
    freiburg.setWeatherViewId(R.id.weather_freiburg);
    freiburg.setForecastButtonId(R.id.button_forecast_freiburg);
    freiburg.setPrefShowInApp("app_show_freiburg");
    freiburg.setPrefShowInWidget("widget_show_freiburg");
    freiburg.setPrefAlert("alert_freiburg");
    locations.add(freiburg);

    return locations;
  }


}
