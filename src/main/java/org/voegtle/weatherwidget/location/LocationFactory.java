package org.voegtle.weatherwidget.location;

import android.content.res.Resources;
import android.net.Uri;
import org.voegtle.weatherwidget.R;

import java.util.ArrayList;
import java.util.List;

public class LocationFactory {
  static public List<WeatherLocation> buildWeatherLocations(Resources res) {
    ArrayList<WeatherLocation> locations = new ArrayList<>();

    WeatherLocation paderborn = new WeatherLocation(LocationIdentifier.Paderborn);
    paderborn.setName(res.getString(R.string.city_paderborn_full));
    paderborn.setShortName(res.getString(R.string.city_paderborn));
    paderborn.setIdentifier("tegelweg8");
    paderborn.setForecastUrl(Uri.parse("http://wetterstationen.meteomedia.de/?station=104300&wahl=vorhersage"));
    paderborn.setWeatherLineId(R.id.line_paderborn);
    paderborn.setWeatherViewId(R.id.weather_paderborn);
    paderborn.setRainIndicatorId(R.id.rain_indicator_paderborn);
    paderborn.setPrefShowInApp("app_show_paderborn");
    paderborn.setPrefShowInWidget("widget_show_paderborn");
    paderborn.setPrefAlert("alert_paderborn");
    locations.add(paderborn);

    WeatherLocation badLippspringe = new WeatherLocation(LocationIdentifier.BadLippspringe);
    badLippspringe.setName(res.getString(R.string.city_bali_full));
    badLippspringe.setShortName(res.getString(R.string.city_bali));
    badLippspringe.setIdentifier("bali");
    badLippspringe.setForecastUrl(Uri.parse("http://wetterstationen.meteomedia.de/?station=104300&wahl=vorhersage"));
    badLippspringe.setWeatherLineId(R.id.line_bali);
    badLippspringe.setWeatherViewId(R.id.weather_bali);
    badLippspringe.setRainIndicatorId(R.id.rain_indicator_bali);
    badLippspringe.setPrefShowInApp("app_show_bali");
    badLippspringe.setPrefShowInWidget("widget_show_bali");
    badLippspringe.setPrefAlert("alert_bali");
    badLippspringe.setVisibleInAppByDefault(true);
    badLippspringe.setVisibleInWidgetByDefault(false);
    locations.add(badLippspringe);

    WeatherLocation bonn = new WeatherLocation(LocationIdentifier.Bonn);
    bonn.setName(res.getString(R.string.city_bonn_full));
    bonn.setShortName(res.getString(R.string.city_bonn));
    bonn.setIdentifier("forstweg17");
    bonn.setForecastUrl(Uri.parse("http://wetterstationen.meteomedia.de/?station=105170&wahl=vorhersage"));
    bonn.setWeatherLineId(R.id.line_bonn);
    bonn.setWeatherViewId(R.id.weather_bonn);
    bonn.setRainIndicatorId(R.id.rain_indicator_bonn);
    bonn.setPrefShowInApp("app_show_bonn");
    bonn.setPrefShowInWidget("widget_show_bonn");
    bonn.setPrefAlert("alert_bonn");
    locations.add(bonn);

    WeatherLocation freiburg = new WeatherLocation(LocationIdentifier.Freiburg);
    freiburg.setName(res.getString(R.string.city_freiburg_full));
    freiburg.setShortName(res.getString(R.string.city_freiburg));
    freiburg.setIdentifier("ochsengasse");
    freiburg.setForecastUrl(Uri.parse("http://wetterstationen.meteomedia.de/?station=108030&wahl=vorhersage"));
    freiburg.setWeatherLineId(R.id.line_freiburg);
    freiburg.setWeatherViewId(R.id.weather_freiburg);
    freiburg.setRainIndicatorId(R.id.rain_indicator_freiburg);
    freiburg.setPrefShowInApp("app_show_freiburg");
    freiburg.setPrefShowInWidget("widget_show_freiburg");
    freiburg.setPrefAlert("alert_freiburg");
    locations.add(freiburg);

    WeatherLocation leopoldshoehe = new WeatherLocation(LocationIdentifier.Leopoldshoehe);
    leopoldshoehe.setName(res.getString(R.string.city_leo_full));
    leopoldshoehe.setShortName(res.getString(R.string.city_leo));
    leopoldshoehe.setIdentifier("leoxity");
    leopoldshoehe.setForecastUrl(Uri.parse("http://wetterstationen.meteomedia.de/?station=103250&wahl=vorhersage"));
    leopoldshoehe.setWeatherLineId(R.id.line_leo);
    leopoldshoehe.setWeatherViewId(R.id.weather_leo);
    leopoldshoehe.setRainIndicatorId(R.id.rain_indicator_leo);
    leopoldshoehe.setPrefShowInApp("app_show_leo");
    leopoldshoehe.setPrefShowInWidget("widget_show_leo");
    leopoldshoehe.setVisibleInAppByDefault(true);
    leopoldshoehe.setVisibleInWidgetByDefault(false);
    leopoldshoehe.setPrefAlert("alert_leo");
    locations.add(leopoldshoehe);

    WeatherLocation magdeburg = new WeatherLocation(LocationIdentifier.Magdeburg);
    magdeburg.setName(res.getString(R.string.city_magdeburg_full));
    magdeburg.setShortName(res.getString(R.string.city_magdeburg));
    magdeburg.setIdentifier("elb");
    magdeburg.setForecastUrl(Uri.parse("http://wetterstationen.meteomedia.de/?station=103610&wahl=vorhersage"));
    magdeburg.setWeatherLineId(R.id.line_magdeburg);
    magdeburg.setWeatherViewId(R.id.weather_magdeburg);
    magdeburg.setRainIndicatorId(R.id.rain_indicator_magdeburg);
    magdeburg.setPrefShowInApp("app_show_magdeburg");
    magdeburg.setPrefShowInWidget("widget_show_magdeburg");
    magdeburg.setVisibleInAppByDefault(true);
    magdeburg.setVisibleInWidgetByDefault(false);
    magdeburg.setPrefAlert("alert_magdeburg");
    locations.add(magdeburg);

    WeatherLocation herzogenaurach = new WeatherLocation(LocationIdentifier.Herzogenaurach);
    herzogenaurach.setName(res.getString(R.string.city_herzo_full));
    herzogenaurach.setShortName(res.getString(R.string.city_herzo));
    herzogenaurach.setIdentifier("herzo");
    herzogenaurach.setForecastUrl(Uri.parse("http://wetterstationen.meteomedia.de/?station=194919&wahl=vorhersage"));
    herzogenaurach.setWeatherLineId(R.id.line_herzogenaurach);
    herzogenaurach.setWeatherViewId(R.id.weather_herzogenaurach);
    herzogenaurach.setRainIndicatorId(R.id.rain_indicator_herzogenaurach);
    herzogenaurach.setPrefShowInApp("app_show_herzo");
    herzogenaurach.setPrefShowInWidget("widget_show_herzo");
    herzogenaurach.setVisibleInAppByDefault(true);
    herzogenaurach.setVisibleInWidgetByDefault(false);
    herzogenaurach.setPrefAlert("alert_herzo");
    locations.add(herzogenaurach);

    WeatherLocation mobil = new WeatherLocation(LocationIdentifier.Mobil);
    mobil.setName(res.getString(R.string.city_mobil_full));
    mobil.setShortName(res.getString(R.string.city_mobil));
    mobil.setIdentifier("instant");
    mobil.setForecastUrl(Uri.parse("http://wetterstationen.meteomedia.de/?station=103250&wahl=vorhersage"));
    mobil.setWeatherLineId(R.id.line_mobil);
    mobil.setWeatherViewId(R.id.weather_mobil);
    mobil.setRainIndicatorId(R.id.rain_indicator_mobil);
    mobil.setPrefShowInApp("app_show_mobil");
    mobil.setPrefShowInWidget("widget_show_mobil");
    mobil.setVisibleInAppByDefault(false);
    mobil.setVisibleInWidgetByDefault(false);
    mobil.setPrefAlert("alert_mobil");
    locations.add(mobil);

    return locations;
  }


}
