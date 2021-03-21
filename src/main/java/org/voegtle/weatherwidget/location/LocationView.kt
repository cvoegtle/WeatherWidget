package org.voegtle.weatherwidget.location

import android.content.Context
import android.content.res.TypedArray
import androidx.core.content.ContextCompat
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.TextView
import kotlinx.android.synthetic.main.view_location.view.*
import org.voegtle.weatherwidget.R
import org.voegtle.weatherwidget.data.Statistics
import org.voegtle.weatherwidget.data.StatisticsSet
import org.voegtle.weatherwidget.data.WeatherData
import org.voegtle.weatherwidget.util.ColorUtil
import org.voegtle.weatherwidget.util.DataFormatter

class LocationView(private val currentContext: Context, attrs: AttributeSet) : LinearLayout(currentContext, attrs) {
  private var imageExpand = ContextCompat.getDrawable(currentContext, R.drawable.ic_action_expand)
  private var imageCollapse = ContextCompat.getDrawable(currentContext, R.drawable.ic_action_collapse)
  private var externalClickListener: OnClickListener? = null
  var diagramListener: OnClickListener? = null
  var forecastListener: OnClickListener? = null

  init {
    val li = currentContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    li.inflate(R.layout.view_location, this, true)

    val attributes = currentContext.theme.obtainStyledAttributes(attrs, R.styleable.LocationView, 0, 0)

    try {
      initializeTextViews(attributes)
      initializeButtons(attributes)
    } finally {
      attributes.recycle()
    }
  }

  private val formatter: DataFormatter = DataFormatter()

  var isExpanded: Boolean = false
    set(expanded) {
      field = expanded
      repaintExpandButton()
    }

  private fun repaintExpandButton() {
    resize_button.setImageDrawable(if (isExpanded) imageCollapse else imageExpand)
    more_data.visibility = if (isExpanded) View.VISIBLE else View.GONE
  }

  private fun initializeButtons(attributes: TypedArray) {
    resize_button.setOnClickListener { arg0 ->
      isExpanded = !isExpanded
      externalClickListener?.onClick(arg0)
    }
    repaintExpandButton()

    val diagrams = attributes.getBoolean(R.styleable.LocationView_diagrams, false)
    diagram_button.visibility = if (diagrams) View.VISIBLE else View.GONE
    diagram_button.setOnClickListener {
      diagramListener?.onClick(this@LocationView)
    }

    val forecast = attributes.getBoolean(R.styleable.LocationView_forecast, true)
    forecast_button.visibility = if (forecast) View.VISIBLE else View.GONE
    forecast_button.setOnClickListener { forecastListener?.onClick(this@LocationView) }
  }

  override fun setOnClickListener(l: OnClickListener?) {
    externalClickListener = l
  }

  private fun initializeTextViews(attributes: TypedArray) {
    caption.text = attributes.getString(R.styleable.LocationView_caption)
  }

  fun setTextSize(textSize: Int) {
    caption.textSize = (textSize + 1).toFloat()
    updateTextSize(grid_current, textSize)
    updateTextSize(more_data, textSize)
  }

  private fun updateTextSize(container: GridLayout, textSize: Int) {
    (0..container.childCount - 1)
        .map { container.getChildAt(it) as TextView }
        .forEach { it.textSize = textSize.toFloat() }
  }


  fun setCaption(captionText: String) {
    caption.text = captionText
  }

  fun setData(data: WeatherData) {
    temperature.text = formatter.formatTemperatureForActivity(data)
    humidity.text = formatter.formatHumidityForActivity(data)

    setBarometer(data.barometer)
    setSolarradiation(data.solarradiation)
    setPowerProduction(data.powerProduction)
    setPowerFeed(data.powerFeed)
    setUVIndex(data.UV)

    setRainData(data.rain, label_rain_last_hour, rain_last_hour)
    setRainData(data.rainToday, label_rain_today, rain_today)
    setWind(data.wind)
    setSolarData(data.watt)
  }

  private fun setBarometer(value: Float?) {
    if (value != null && value > 0.0) {
      show(label_barometer, barometer)
      barometer.text = formatter.formatBarometer(value)
    } else {
      hide(label_barometer, barometer)
    }
  }

  private fun setSolarradiation(value: Float?) {
    if (value != null && value > 0.0) {
      show(label_solarradiation, solarradiation)
      solarradiation.text = formatter.formatSolarradiation(value)
    } else {
      hide(label_solarradiation, solarradiation)
    }
  }

  private fun setPowerProduction(powerProduction: Float?) {
    if (powerProduction != null && powerProduction >= 1.0) {
      show(label_power_production, power_production)
      power_production.text = formatter.formatWatt(powerProduction)
    } else {
      hide(label_power_production, power_production)
    }
  }

  private fun setPowerFeed(powerFeed: Float?) {
    if (powerFeed != null && powerFeed >= 5.0) {
      show(label_power_feed, power_feed)
      power_feed.text = formatter.formatWatt(powerFeed)
    } else {
      hide(label_power_feed, power_feed)
    }
  }

  private fun setUVIndex(value: Float?) {
    if (value != null && value > 0.0) {
      show(label_uv, uv)
      uv.text = formatter.formatInteger(value)
    } else {
      hide(label_uv, uv)
    }
  }

  private fun setSolarData(watt: Float?) {
    if (watt != null && watt > 0.0) {
      show(label_solar_output, solar_output)
      solar_output.text = formatter.formatWatt(watt)
    } else {
      hide(label_solar_output, solar_output)
    }
  }

  private fun setRainData(value: Float?, rainLabel: TextView, rain: TextView) {
    rainLabel.visibility = if (value != null && value > 0.0f) View.VISIBLE else View.GONE
    rain.visibility = if (value != null && value > 0.0f) View.VISIBLE else View.GONE
    rain.text = if (value != null && value > 0.0f) formatter.formatRain(value) else ""
  }

  private fun setWind(value: Float?) {
    if (value != null && value >= 1.0) {
      show(label_wind_speed, wind_speed)
      wind_speed.text = formatter.formatWind(value)
    } else {
      hide(label_wind_speed, wind_speed)
    }
  }

  fun setMoreData(statistics: Statistics) {
    val kindOfStation = statistics.kind
    setSolarCaptions(kindOfStation)

    val today = statistics[Statistics.TimeRange.today]
    updateStatistics(today, kindOfStation, today_rain, today_min_temperature, today_max_temperature, today_kwh, today_solar)

    val yesterday = statistics[Statistics.TimeRange.yesterday]
    updateStatistics(yesterday, kindOfStation, yesterday_rain, yesterday_min_temperature, yesterday_max_temperature, yesterday_kwh, yesterday_solar)

    val week = statistics[Statistics.TimeRange.last7days]
    updateStatistics(week, kindOfStation, week_rain, week_min_temperature, week_max_temperature, week_kwh, week_solar)

    val month = statistics[Statistics.TimeRange.last30days]
    updateStatistics(month, kindOfStation, month_rain, month_min_temperature, month_max_temperature, month_kwh, month_solar)
  }

  private fun updateStatistics(stats: StatisticsSet?, kind: String, rainView: TextView, minView: TextView, maxView: TextView, kwhView: TextView,
                               solarView: TextView) {
    rainView.text = ""
    minView.text = ""
    maxView.text = ""
    kwhView.text = ""
    solarView.text = ""
    stats?.let {
      it.rain?.let  {
        rainView.text = formatter.formatRain(it)
        show(caption_rain)
      }
      minView.text = formatter.formatTemperature(it.minTemperature)
      maxView.text = formatter.formatTemperature(it.maxTemperature)
      stats.kwh?.let {
        kwhView.text = formatter.formatKwh(it)
        show(caption_kwh)
      }
      stats.solarRadiationMax?.let {
        show(caption_solar)
        if (kind == "withSolarPower") {
          solarView.text = formatter.formatWatt(it)
        } else {
          solarView.text = formatter.formatSolarradiation(it)
        }
      }
    }
  }

  private fun setSolarCaptions(kindOfStation: String) {
    if (kindOfStation == "withSolarPower") {
      caption_kwh.text = context.resources.getString(R.string.kwh)
      caption_solar.text = context.resources.getString(R.string.max_power_caption)
    } else {
      caption_kwh.text = context.resources.getString(R.string.solar_cummulated_caption)
      caption_solar.text = context.resources.getString(R.string.solar_caption)
    }

  }


  fun configureSymbols(useDarkSymbols: Boolean) {
    if (useDarkSymbols) {
      imageCollapse = ContextCompat.getDrawable(currentContext, R.drawable.ic_action_collapse_dark)
      imageExpand = ContextCompat.getDrawable(currentContext, R.drawable.ic_action_expand_dark)
      repaintExpandButton()

      val imageForecastDark = ContextCompat.getDrawable(currentContext, R.drawable.ic_action_forecast_dark)
      forecast_button.setImageDrawable(imageForecastDark)
      val imageDiagramDark = ContextCompat.getDrawable(currentContext, R.drawable.ic_action_picture_dark)
      diagram_button.setImageDrawable(imageDiagramDark)
    }
  }

  fun setTextColor(color: Int) {
    caption.setTextColor(color)
  }

  fun highlight(highlight: Boolean) {
    if (highlight) {
      setBackgroundColor(ColorUtil.highlight())
    }
  }

  private fun hide(vararg view: TextView) {
    for (v in view) {
      v.visibility = View.GONE
    }
  }

  private fun show(vararg view: TextView) {
    for (v in view) {
      v.visibility = View.VISIBLE
    }
  }

}
