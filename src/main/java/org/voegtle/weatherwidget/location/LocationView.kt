package org.voegtle.weatherwidget.location

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.support.v4.content.ContextCompat
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
  private var externalClickListener: View.OnClickListener? = null
  var diagramListener: View.OnClickListener? = null
  var forecastListener: View.OnClickListener? = null

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

  override fun setOnClickListener(listener: View.OnClickListener) {
    externalClickListener = listener
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

    setRainData(data.rain, label_rain_last_hour, rain_last_hour)
    setRainData(data.rainToday, label_rain_today, rain_today)
    setWind(data.wind)
    setSolarData(data.watt)
  }

  private fun setSolarData(watt: Float?) {
    if (watt != null && watt > 0.0) {
      label_solar_output.visibility = View.VISIBLE
      solar_output.visibility = View.VISIBLE
      solar_output.text = formatter.formatWatt(watt)
    } else {
      label_solar_output.visibility = View.GONE
      solar_output.visibility = View.GONE
    }
  }

  private fun setRainData(value: Float?, rainLabel: TextView, rain: TextView) {
    rainLabel.visibility = if (value != null) View.VISIBLE else View.GONE
    rain.visibility = if (value != null) View.VISIBLE else View.GONE
    rain.text = if (value != null) formatter.formatRain(value) else ""
  }

  private fun setWind(value: Float?) {
    if (value != null && value >= 1.0) {
      label_wind_speed.visibility = View.VISIBLE
      wind_speed.visibility = View.VISIBLE
      wind_speed.text = formatter.formatWind(value)
    } else {
      label_wind_speed.visibility = View.GONE
      wind_speed.visibility = View.GONE
    }
  }

  fun setMoreData(statistics: Statistics) {
    val today = statistics[Statistics.TimeRange.today]
    updateStatistics(today, today_rain, today_min_temperature, today_max_temperature, today_kwh)

    val yesterday = statistics[Statistics.TimeRange.yesterday]
    updateStatistics(yesterday, yesterday_rain, yesterday_min_temperature, yesterday_max_temperature, yesterday_kwh)

    val week = statistics[Statistics.TimeRange.last7days]
    updateStatistics(week, week_rain, week_min_temperature, week_max_temperature, week_kwh)

    val month = statistics[Statistics.TimeRange.last30days]
    updateStatistics(month, month_rain, month_min_temperature, month_max_temperature, month_kwh)
  }

  private fun updateStatistics(stats: StatisticsSet?, rainView: TextView, minView: TextView, maxView: TextView,
                               kwhView: TextView) {
    rainView.text = ""
    minView.text = ""
    maxView.text = ""
    kwhView.text = ""
    if (stats != null) {
      if (stats.rain != null) {
        rainView.text = formatter.formatRain(stats.rain)
        caption_rain.visibility = View.VISIBLE
      }
      minView.text = formatter.formatTemperature(stats.minTemperature)
      maxView.text = formatter.formatTemperature(stats.maxTemperature)
      if (stats.kwh != null) {
        kwhView.text = formatter.formatKwh(stats.kwh)
        caption_kwh.visibility = View.VISIBLE
      }
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

}
