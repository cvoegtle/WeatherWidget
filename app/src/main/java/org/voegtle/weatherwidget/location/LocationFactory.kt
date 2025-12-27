package org.voegtle.weatherwidget.location

import android.content.res.Resources
import androidx.core.net.toUri
import org.voegtle.weatherwidget.R

object LocationFactory {
  fun buildWeatherLocations(res: Resources): List<WeatherLocation> {
    val locations = ArrayList<WeatherLocation>()

    locations.add(WeatherLocation(LocationIdentifier.Paderborn,
                                  name = res.getString(R.string.city_paderborn_full),
                                  shortName = res.getString(R.string.city_paderborn),
                                  identifier = "tegelweg8",
                                  forecastUrl = "http://wetterstationen.meteomedia.de/?station=104300&wahl=vorhersage".toUri(),
                                  prefShowInApp = "app_show_paderborn",
                                  prefShowInWidget = "widget_show_paderborn",
                                  prefFavorite = "favorite_paderborn"))

    locations.add(WeatherLocation(LocationIdentifier.BadLippspringe,
                                  name = res.getString(R.string.city_bali_full),
                                  shortName = res.getString(R.string.city_bali),
                                  identifier = "bali",
                                  forecastUrl = "http://wetterstationen.meteomedia.de/?station=104300&wahl=vorhersage".toUri(),
                                  prefShowInApp = "app_show_bali",
                                  prefShowInWidget = "widget_show_bali",
                                  prefFavorite = "favorite_bali",
                                  isVisibleInWidgetByDefault = false))

    locations.add(WeatherLocation(LocationIdentifier.Bonn,
                                  name = res.getString(R.string.city_bonn_full),
                                  shortName = res.getString(R.string.city_bonn),
                                  identifier = "forstweg17",
                                  forecastUrl = "http://wetterstationen.meteomedia.de/?station=105170&wahl=vorhersage".toUri(),
                                  prefShowInApp = "app_show_bonn",
                                  prefShowInWidget = "widget_show_bonn",
                                  prefFavorite = "favorite_bonn"))

    locations.add(WeatherLocation(LocationIdentifier.Freiburg,
                                  name = res.getString(R.string.city_freiburg_full),
                                  shortName = res.getString(R.string.city_freiburg),
                                  identifier = "ochsengasse",
                                  forecastUrl = "http://wetterstationen.meteomedia.de/?station=108030&wahl=vorhersage".toUri(),
                                  prefShowInApp = "app_show_freiburg",
                                  prefShowInWidget = "widget_show_freiburg",
                                  prefFavorite = "favorite_freiburg"))

    locations.add(WeatherLocation(LocationIdentifier.Leopoldshoehe,
                                  name = res.getString(R.string.city_leo_full),
                                  shortName = res.getString(R.string.city_leo),
                                  identifier = "leoxity",
                                  forecastUrl = "http://wetterstationen.meteomedia.de/?station=103250&wahl=vorhersage".toUri(),
                                  prefShowInApp = "app_show_leo",
                                  prefShowInWidget = "widget_show_leo",
                                  isVisibleInWidgetByDefault = false,
                                  prefFavorite = "favorite_leo"))

    locations.add(WeatherLocation(LocationIdentifier.Magdeburg,
                                  name = res.getString(R.string.city_magdeburg_full),
                                  shortName = res.getString(R.string.city_magdeburg),
                                  identifier = "elb",
                                  forecastUrl = "http://wetterstationen.meteomedia.de/?station=103610&wahl=vorhersage".toUri(),
                                  prefShowInApp = "app_show_magdeburg",
                                  prefShowInWidget = "widget_show_magdeburg",
                                  isVisibleInWidgetByDefault = false,
                                  prefFavorite = "favorite_magdeburg"))

    locations.add(WeatherLocation(LocationIdentifier.Herzogenaurach,
                                  name = res.getString(R.string.city_herzo_full),
                                  shortName = res.getString(R.string.city_herzo),
                                  identifier = "herzo",
                                  forecastUrl = "http://wetterstationen.meteomedia.de/?station=194919&wahl=vorhersage".toUri(),
                                  prefShowInApp = "app_show_herzo",
                                  prefShowInWidget = "widget_show_herzo",
                                  isVisibleInWidgetByDefault = false,
                                  prefFavorite = "favorite_herzo"))

    locations.add(WeatherLocation(LocationIdentifier.Shenzhen,
                                  name = res.getString(R.string.city_shenzhen_full),
                                  shortName = res.getString(R.string.city_shenzhen),
                                  identifier = "shenzhen",
                                  forecastUrl = "http://www.wetter.com/china/shenzhen/CN0GD0012.html".toUri(),
                                  prefShowInApp = "app_show_shenzhen",
                                  prefShowInWidget = "widget_show_shenzhen",
                                  isVisibleInWidgetByDefault = false,
                                  prefFavorite = "favorite_shenzhen"))

    locations.add(WeatherLocation(LocationIdentifier.Mobil,
                                  name = res.getString(R.string.city_mobil_full),
                                  shortName = res.getString(R.string.city_mobil),
                                  identifier = "instant",
                                  forecastUrl = "http://wetterstationen.meteomedia.de/?station=103250&wahl=vorhersage".toUri(),
                                  prefShowInApp = "app_show_mobil",
                                  prefShowInWidget = "widget_show_mobil",
                                  isVisibleInAppByDefault = false,
                                  isVisibleInWidgetByDefault = false,
                                  prefFavorite = "favorite_mobil"))

    return locations
  }


}
