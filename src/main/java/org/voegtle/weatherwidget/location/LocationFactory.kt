package org.voegtle.weatherwidget.location

import android.content.res.Resources
import android.net.Uri
import org.voegtle.weatherwidget.R

import java.util.ArrayList

object LocationFactory {
  fun buildWeatherLocations(res: Resources): List<WeatherLocation> {
    val locations = ArrayList<WeatherLocation>()

    val paderborn = WeatherLocation(LocationIdentifier.Paderborn)
    paderborn.name = res.getString(R.string.city_paderborn_full)
    paderborn.shortName = res.getString(R.string.city_paderborn)
    paderborn.identifier = "tegelweg8"
    paderborn.forecastUrl = Uri.parse("http://wetterstationen.meteomedia.de/?station=104300&wahl=vorhersage")
    paderborn.weatherLineId = R.id.line_paderborn
    paderborn.weatherViewId = R.id.weather_paderborn
    paderborn.rainIndicatorId = R.id.rain_indicator_paderborn
    paderborn.prefShowInApp = "app_show_paderborn"
    paderborn.prefShowInWidget = "widget_show_paderborn"
    paderborn.prefAlert = "alert_paderborn"
    locations.add(paderborn)

    val badLippspringe = WeatherLocation(LocationIdentifier.BadLippspringe)
    badLippspringe.name = res.getString(R.string.city_bali_full)
    badLippspringe.shortName = res.getString(R.string.city_bali)
    badLippspringe.identifier = "bali"
    badLippspringe.forecastUrl = Uri.parse("http://wetterstationen.meteomedia.de/?station=104300&wahl=vorhersage")
    badLippspringe.weatherLineId = R.id.line_bali
    badLippspringe.weatherViewId = R.id.weather_bali
    badLippspringe.rainIndicatorId = R.id.rain_indicator_bali
    badLippspringe.prefShowInApp = "app_show_bali"
    badLippspringe.prefShowInWidget = "widget_show_bali"
    badLippspringe.prefAlert = "alert_bali"
    badLippspringe.isVisibleInAppByDefault = true
    badLippspringe.isVisibleInWidgetByDefault = false
    locations.add(badLippspringe)

    val bonn = WeatherLocation(LocationIdentifier.Bonn)
    bonn.name = res.getString(R.string.city_bonn_full)
    bonn.shortName = res.getString(R.string.city_bonn)
    bonn.identifier = "forstweg17"
    bonn.forecastUrl = Uri.parse("http://wetterstationen.meteomedia.de/?station=105170&wahl=vorhersage")
    bonn.weatherLineId = R.id.line_bonn
    bonn.weatherViewId = R.id.weather_bonn
    bonn.rainIndicatorId = R.id.rain_indicator_bonn
    bonn.prefShowInApp = "app_show_bonn"
    bonn.prefShowInWidget = "widget_show_bonn"
    bonn.prefAlert = "alert_bonn"
    locations.add(bonn)

    val freiburg = WeatherLocation(LocationIdentifier.Freiburg)
    freiburg.name = res.getString(R.string.city_freiburg_full)
    freiburg.shortName = res.getString(R.string.city_freiburg)
    freiburg.identifier = "ochsengasse"
    freiburg.forecastUrl = Uri.parse("http://wetterstationen.meteomedia.de/?station=108030&wahl=vorhersage")
    freiburg.weatherLineId = R.id.line_freiburg
    freiburg.weatherViewId = R.id.weather_freiburg
    freiburg.rainIndicatorId = R.id.rain_indicator_freiburg
    freiburg.prefShowInApp = "app_show_freiburg"
    freiburg.prefShowInWidget = "widget_show_freiburg"
    freiburg.prefAlert = "alert_freiburg"
    locations.add(freiburg)

    val leopoldshoehe = WeatherLocation(LocationIdentifier.Leopoldshoehe)
    leopoldshoehe.name = res.getString(R.string.city_leo_full)
    leopoldshoehe.shortName = res.getString(R.string.city_leo)
    leopoldshoehe.identifier = "leoxity"
    leopoldshoehe.forecastUrl = Uri.parse("http://wetterstationen.meteomedia.de/?station=103250&wahl=vorhersage")
    leopoldshoehe.weatherLineId = R.id.line_leo
    leopoldshoehe.weatherViewId = R.id.weather_leo
    leopoldshoehe.rainIndicatorId = R.id.rain_indicator_leo
    leopoldshoehe.prefShowInApp = "app_show_leo"
    leopoldshoehe.prefShowInWidget = "widget_show_leo"
    leopoldshoehe.isVisibleInAppByDefault = true
    leopoldshoehe.isVisibleInWidgetByDefault = false
    leopoldshoehe.prefAlert = "alert_leo"
    locations.add(leopoldshoehe)

    val magdeburg = WeatherLocation(LocationIdentifier.Magdeburg)
    magdeburg.name = res.getString(R.string.city_magdeburg_full)
    magdeburg.shortName = res.getString(R.string.city_magdeburg)
    magdeburg.identifier = "elb"
    magdeburg.forecastUrl = Uri.parse("http://wetterstationen.meteomedia.de/?station=103610&wahl=vorhersage")
    magdeburg.weatherLineId = R.id.line_magdeburg
    magdeburg.weatherViewId = R.id.weather_magdeburg
    magdeburg.rainIndicatorId = R.id.rain_indicator_magdeburg
    magdeburg.prefShowInApp = "app_show_magdeburg"
    magdeburg.prefShowInWidget = "widget_show_magdeburg"
    magdeburg.isVisibleInAppByDefault = true
    magdeburg.isVisibleInWidgetByDefault = false
    magdeburg.prefAlert = "alert_magdeburg"
    locations.add(magdeburg)

    val herzogenaurach = WeatherLocation(LocationIdentifier.Herzogenaurach)
    herzogenaurach.name = res.getString(R.string.city_herzo_full)
    herzogenaurach.shortName = res.getString(R.string.city_herzo)
    herzogenaurach.identifier = "herzo"
    herzogenaurach.forecastUrl = Uri.parse("http://wetterstationen.meteomedia.de/?station=194919&wahl=vorhersage")
    herzogenaurach.weatherLineId = R.id.line_herzogenaurach
    herzogenaurach.weatherViewId = R.id.weather_herzogenaurach
    herzogenaurach.rainIndicatorId = R.id.rain_indicator_herzogenaurach
    herzogenaurach.prefShowInApp = "app_show_herzo"
    herzogenaurach.prefShowInWidget = "widget_show_herzo"
    herzogenaurach.isVisibleInAppByDefault = true
    herzogenaurach.isVisibleInWidgetByDefault = false
    herzogenaurach.prefAlert = "alert_herzo"
    locations.add(herzogenaurach)

    val shenzhen = WeatherLocation(LocationIdentifier.Shenzhen)
    shenzhen.name = res.getString(R.string.city_shenzhen_full)
    shenzhen.shortName = res.getString(R.string.city_shenzhen)
    shenzhen.identifier = "shenzhen"
    shenzhen.forecastUrl = Uri.parse("http://www.wetter.com/china/shenzhen/CN0GD0012.html")
    shenzhen.weatherLineId = R.id.line_shenzhen
    shenzhen.weatherViewId = R.id.weather_shenzhen
    shenzhen.rainIndicatorId = R.id.rain_indicator_shenzhen
    shenzhen.prefShowInApp = "app_show_shenzhen"
    shenzhen.prefShowInWidget = "widget_show_shenzhen"
    shenzhen.isVisibleInAppByDefault = true
    shenzhen.isVisibleInWidgetByDefault = false
    shenzhen.prefAlert = "alert_shenzhen"
    locations.add(shenzhen)

    val mobil = WeatherLocation(LocationIdentifier.Mobil)
    mobil.name = res.getString(R.string.city_mobil_full)
    mobil.shortName = res.getString(R.string.city_mobil)
    mobil.identifier = "instant"
    mobil.forecastUrl = Uri.parse("http://wetterstationen.meteomedia.de/?station=103250&wahl=vorhersage")
    mobil.weatherLineId = R.id.line_mobil
    mobil.weatherViewId = R.id.weather_mobil
    mobil.rainIndicatorId = R.id.rain_indicator_mobil
    mobil.prefShowInApp = "app_show_mobil"
    mobil.prefShowInWidget = "widget_show_mobil"
    mobil.isVisibleInAppByDefault = false
    mobil.isVisibleInWidgetByDefault = false
    mobil.prefAlert = "alert_mobil"
    locations.add(mobil)

    return locations
  }


}
