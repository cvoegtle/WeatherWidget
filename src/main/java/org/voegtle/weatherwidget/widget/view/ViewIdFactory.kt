package org.voegtle.weatherwidget.widget.view

import org.voegtle.weatherwidget.R

object ViewIdFactory {
  fun buildViewIds(): ArrayList<ViewId> {
    val widgetViews = ArrayList<ViewId>()

    widgetViews.add(ViewId(line = R.id.line_1, rain = R.id.rain_indicator_1, weather = R.id.weather_1))
    widgetViews.add(ViewId(line = R.id.line_2, rain = R.id.rain_indicator_2, weather = R.id.weather_2))
    widgetViews.add(ViewId(line = R.id.line_3, rain = R.id.rain_indicator_3, weather = R.id.weather_3))
    widgetViews.add(ViewId(line = R.id.line_4, rain = R.id.rain_indicator_4, weather = R.id.weather_4))
    widgetViews.add(ViewId(line = R.id.line_5, rain = R.id.rain_indicator_5, weather = R.id.weather_5))
    widgetViews.add(ViewId(line = R.id.line_6, rain = R.id.rain_indicator_6, weather = R.id.weather_6))
    widgetViews.add(ViewId(line = R.id.line_7, rain = R.id.rain_indicator_7, weather = R.id.weather_7))
    widgetViews.add(ViewId(line = R.id.line_8, rain = R.id.rain_indicator_8, weather = R.id.weather_8))
    widgetViews.add(ViewId(line = R.id.line_9, rain = R.id.rain_indicator_9, weather = R.id.weather_9))

    return widgetViews
  }
}