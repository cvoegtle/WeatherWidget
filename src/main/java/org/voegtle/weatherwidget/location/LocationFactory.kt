package org.voegtle.weatherwidget.location

import android.content.res.Resources
import android.net.Uri
import org.voegtle.weatherwidget.R
import java.util.*

object LocationFactory {
  fun buildWeatherLocations(res: Resources): List<WeatherLocation> {
    val locations = ArrayList<WeatherLocation>()

    locations.add(WeatherLocation(LocationIdentifier.Paderborn,
        name = res.getString(R.string.city_paderborn_full),
        shortName = res.getString(R.string.city_paderborn),
        identifier = "tegelweg8",
        forecastUrl = Uri.parse("http://wetterstationen.meteomedia.de/?station=104300&wahl=vorhersage"),
        weatherLineId = R.id.line_paderborn,
        weatherViewId = R.id.weather_paderborn,
        rainIndicatorId = R.id.rain_indicator_paderborn,
        prefShowInApp = "app_show_paderborn",
        prefShowInWidget = "widget_show_paderborn",
        prefAlert = "alert_paderborn"))

    locations.add(WeatherLocation(LocationIdentifier.BadLippspringe,
        name = res.getString(R.string.city_bali_full),
        shortName = res.getString(R.string.city_bali),
        identifier = "bali",
        forecastUrl = Uri.parse("http://wetterstationen.meteomedia.de/?station=104300&wahl=vorhersage"),
        weatherLineId = R.id.line_bali,
        weatherViewId = R.id.weather_bali,
        rainIndicatorId = R.id.rain_indicator_bali,
        prefShowInApp = "app_show_bali",
        prefShowInWidget = "widget_show_bali",
        prefAlert = "alert_bali",
        isVisibleInWidgetByDefault = false))

    locations.add(WeatherLocation(LocationIdentifier.Bonn,
        name = res.getString(R.string.city_bonn_full),
        shortName = res.getString(R.string.city_bonn),
        identifier = "forstweg17",
        forecastUrl = Uri.parse("http://wetterstationen.meteomedia.de/?station=105170&wahl=vorhersage"),
        weatherLineId = R.id.line_bonn,
        weatherViewId = R.id.weather_bonn,
        rainIndicatorId = R.id.rain_indicator_bonn,
        prefShowInApp = "app_show_bonn",
        prefShowInWidget = "widget_show_bonn",
        prefAlert = "alert_bonn"))

    locations.add(WeatherLocation(LocationIdentifier.Freiburg,
        name = res.getString(R.string.city_freiburg_full),
        shortName = res.getString(R.string.city_freiburg),
        identifier = "ochsengasse",
        forecastUrl = Uri.parse("http://wetterstationen.meteomedia.de/?station=108030&wahl=vorhersage"),
        weatherLineId = R.id.line_freiburg,
        weatherViewId = R.id.weather_freiburg,
        rainIndicatorId = R.id.rain_indicator_freiburg,
        prefShowInApp = "app_show_freiburg",
        prefShowInWidget = "widget_show_freiburg",
        prefAlert = "alert_freiburg"))

    locations.add(WeatherLocation(LocationIdentifier.Leopoldshoehe,
        name = res.getString(R.string.city_leo_full),
        shortName = res.getString(R.string.city_leo),
        identifier = "leoxity",
        forecastUrl = Uri.parse("http://wetterstationen.meteomedia.de/?station=103250&wahl=vorhersage"),
        weatherLineId = R.id.line_leo,
        weatherViewId = R.id.weather_leo,
        rainIndicatorId = R.id.rain_indicator_leo,
        prefShowInApp = "app_show_leo",
        prefShowInWidget = "widget_show_leo",
        isVisibleInWidgetByDefault = false,
        prefAlert = "alert_leo"))

    locations.add(WeatherLocation(LocationIdentifier.Magdeburg,
        name = res.getString(R.string.city_magdeburg_full),
        shortName = res.getString(R.string.city_magdeburg),
        identifier = "elb",
        forecastUrl = Uri.parse("http://wetterstationen.meteomedia.de/?station=103610&wahl=vorhersage"),
        weatherLineId = R.id.line_magdeburg,
        weatherViewId = R.id.weather_magdeburg,
        rainIndicatorId = R.id.rain_indicator_magdeburg,
        prefShowInApp = "app_show_magdeburg",
        prefShowInWidget = "widget_show_magdeburg",
        isVisibleInWidgetByDefault = false,
        prefAlert = "alert_magdeburg"))

    locations.add(WeatherLocation(LocationIdentifier.Herzogenaurach,
        name = res.getString(R.string.city_herzo_full),
        shortName = res.getString(R.string.city_herzo),
        identifier = "herzo",
        forecastUrl = Uri.parse("http://wetterstationen.meteomedia.de/?station=194919&wahl=vorhersage"),
        weatherLineId = R.id.line_herzogenaurach,
        weatherViewId = R.id.weather_herzogenaurach,
        rainIndicatorId = R.id.rain_indicator_herzogenaurach,
        prefShowInApp = "app_show_herzo",
        prefShowInWidget = "widget_show_herzo",
        isVisibleInWidgetByDefault = false,
        prefAlert = "alert_herzo"))

    locations.add(WeatherLocation(LocationIdentifier.Shenzhen,
        name = res.getString(R.string.city_shenzhen_full),
        shortName = res.getString(R.string.city_shenzhen),
        identifier = "shenzhen",
        forecastUrl = Uri.parse("http://www.wetter.com/china/shenzhen/CN0GD0012.html"),
        weatherLineId = R.id.line_shenzhen,
        weatherViewId = R.id.weather_shenzhen,
        rainIndicatorId = R.id.rain_indicator_shenzhen,
        prefShowInApp = "app_show_shenzhen",
        prefShowInWidget = "widget_show_shenzhen",
        isVisibleInWidgetByDefault = false,
        prefAlert = "alert_shenzhen"))

    locations.add(WeatherLocation(LocationIdentifier.Mobil,
        name = res.getString(R.string.city_mobil_full),
        shortName = res.getString(R.string.city_mobil),
        identifier = "instant",
        forecastUrl = Uri.parse("http://wetterstationen.meteomedia.de/?station=103250&wahl=vorhersage"),
        weatherLineId = R.id.line_mobil,
        weatherViewId = R.id.weather_mobil,
        rainIndicatorId = R.id.rain_indicator_mobil,
        prefShowInApp = "app_show_mobil",
        prefShowInWidget = "widget_show_mobil",
        isVisibleInAppByDefault = false,
        isVisibleInWidgetByDefault = false,
        prefAlert = "alert_mobil"))

    return locations
  }


}
