package org.voegtle.weatherwidget.location

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.GridLayout
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import org.voegtle.weatherwidget.R
import org.voegtle.weatherwidget.data.Statistics
import org.voegtle.weatherwidget.data.StatisticsSet
import org.voegtle.weatherwidget.data.WeatherData
import org.voegtle.weatherwidget.util.ColorUtil
import org.voegtle.weatherwidget.util.DataFormatter

class LocationView(private val currentContext: Context, attrs: AttributeSet) : LinearLayout(currentContext, attrs) {
  private val captionView: TextView
  private var resizeButton: ImageButton? = null
  private var diagramButton: ImageButton? = null
  private var forecastButton: ImageButton? = null
  private val moreData: GridLayout
  private val kwhCaptionView: TextView
  private val rainCaptionView: TextView

  var isExpanded: Boolean = false
    set(expanded) {
      field = expanded
      resizeButton!!.setImageDrawable(if (expanded) imageCollapse else imageExpand)
      moreData.visibility = if (expanded) View.VISIBLE else View.GONE
    }
  private var imageExpand: Drawable? = null
  private var imageCollapse: Drawable? = null
  private var externalClickListener: View.OnClickListener? = null
  private var diagramListener: View.OnClickListener? = null
  private var forecastListener: View.OnClickListener? = null

  private val formatter: DataFormatter


  init {
    formatter = DataFormatter()

    val li = currentContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    li.inflate(R.layout.view_location, this, true)

    captionView = findViewById(R.id.caption) as TextView
    moreData = findViewById(R.id.more_data) as GridLayout
    kwhCaptionView = findViewById(R.id.caption_kwh) as TextView
    rainCaptionView = findViewById(R.id.caption_rain) as TextView


    imageCollapse = ContextCompat.getDrawable(currentContext, R.drawable.ic_action_collapse)
    imageExpand = ContextCompat.getDrawable(currentContext, R.drawable.ic_action_expand)

    val attributes = currentContext.theme.obtainStyledAttributes(
        attrs, R.styleable.LocationView, 0, 0)

    try {
      initializeTextViews(attributes)
      initializeButtons(attributes)
    } finally {
      attributes.recycle()
    }
  }

  private fun initializeButtons(attributes: TypedArray) {
    resizeButton = findViewById(R.id.resize_button) as ImageButton
    resizeButton!!.setOnClickListener { arg0 ->
      isExpanded = !isExpanded
      if (externalClickListener != null) {
        externalClickListener!!.onClick(arg0)
      }
    }

    val diagrams = attributes.getBoolean(R.styleable.LocationView_diagrams, false)
    diagramButton = findViewById(R.id.diagram_button) as ImageButton
    diagramButton!!.visibility = if (diagrams) View.VISIBLE else View.GONE
    diagramButton!!.setOnClickListener {
      if (diagramListener != null) {
        diagramListener!!.onClick(this@LocationView)
      }
    }

    val forecast = attributes.getBoolean(R.styleable.LocationView_forecast, true)
    forecastButton = findViewById(R.id.forecast_button) as ImageButton
    forecastButton!!.visibility = if (forecast) View.VISIBLE else View.GONE
    forecastButton!!.setOnClickListener {
      if (forecastListener != null)
        forecastListener!!.onClick(this@LocationView)
    }

  }

  override fun setOnClickListener(listener: View.OnClickListener) {
    externalClickListener = listener
  }

  fun setDiagramsOnClickListener(listener: View.OnClickListener) {
    diagramListener = listener
  }

  fun setForecastOnClickListener(listener: View.OnClickListener) {
    forecastListener = listener
  }

  private fun initializeTextViews(attributes: TypedArray) {
    val caption = attributes.getString(R.styleable.LocationView_caption)
    captionView.text = caption

  }

  fun setTextSize(textSize: Int) {
    captionView.textSize = (textSize + 1).toFloat()
    updateTextSize(findViewById(R.id.grid_current) as GridLayout, textSize)
    updateTextSize(findViewById(R.id.more_data) as GridLayout, textSize)
  }

  private fun updateTextSize(container: GridLayout, textSize: Int) {
    for (i in 0..container.childCount - 1) {
      val view = container.getChildAt(i) as TextView
      view.textSize = textSize.toFloat()
    }
  }


  fun setCaption(caption: String) {
    captionView.text = caption
  }

  fun setData(data: WeatherData) {
    val temperature = findViewById(R.id.temperature) as TextView
    temperature.text = formatter.formatTemperatureForActivity(data)

    val humidity = findViewById(R.id.humidity) as TextView
    humidity.text = formatter.formatHumidityForActivity(data)

    setRainData(data.rain, R.id.label_rain_last_hour, R.id.rain_last_hour)
    setRainData(data.rainToday, R.id.label_rain_today, R.id.rain_today)
    setWind(data.wind, R.id.label_wind_speed, R.id.wind_speed)
    setSolarData(data.watt, R.id.label_solar_output, R.id.solar_output)
  }

  private fun setSolarData(watt: Float?, labelId: Int, dataId: Int) {
    val solarLabel = findViewById(labelId) as TextView
    val solar = findViewById(dataId) as TextView
    if (watt != null && watt.equals(0.0)) {
      solarLabel.visibility = View.VISIBLE
      solar.visibility = View.VISIBLE
      solar.text = formatter.formatWatt(watt)
    } else {
      solarLabel.visibility = View.GONE
      solar.visibility = View.GONE
    }
  }

  private fun setRainData(value: Float?, labelId: Int, dataId: Int) {
    val rainLabel = findViewById(labelId) as TextView
    val rain = findViewById(dataId) as TextView
    if (value != null) {
      rainLabel.visibility = View.VISIBLE
      rain.visibility = View.VISIBLE
      rain.text = formatter.formatRain(value)
    } else {
      rainLabel.visibility = View.GONE
      rain.visibility = View.GONE
    }
  }

  private fun setWind(value: Float?, labelId: Int, dataId: Int) {
    val windLabel = findViewById(labelId) as TextView
    val wind = findViewById(dataId) as TextView

    if (value != null && value >= 1.0) {
      windLabel.visibility = View.VISIBLE
      wind.visibility = View.VISIBLE
      wind.text = formatter.formatWind(value)
    } else {
      windLabel.visibility = View.GONE
      wind.visibility = View.GONE
    }

  }

  fun setMoreData(statistics: Statistics?) {
    var statistics = statistics
    if (statistics == null) {
      statistics = Statistics()
    }

    val today = statistics[Statistics.TimeRange.today]
    updateStatistics(today, R.id.today_rain, R.id.today_min_temperature, R.id.today_max_temperature, R.id.today_kwh)

    val yesterday = statistics[Statistics.TimeRange.yesterday]
    updateStatistics(yesterday, R.id.yesterday_rain, R.id.yesterday_min_temperature, R.id.yesterday_max_temperature, R.id.yesterday_kwh)

    val week = statistics[Statistics.TimeRange.last7days]
    updateStatistics(week, R.id.week_rain, R.id.week_min_temperature, R.id.week_max_temperature, R.id.week_kwh)

    val month = statistics[Statistics.TimeRange.last30days]
    updateStatistics(month, R.id.month_rain, R.id.month_min_temperature, R.id.month_max_temperature, R.id.month_kwh)
  }

  private fun updateStatistics(stats: StatisticsSet?, rainId: Int, minTemperatureId: Int, maxTemperatureId: Int, kwhId: Int) {
    val rainView = findViewById(rainId) as TextView
    val minView = findViewById(minTemperatureId) as TextView
    val maxView = findViewById(maxTemperatureId) as TextView
    val kwhView = findViewById(kwhId) as TextView
    rainView.text = ""
    minView.text = ""
    maxView.text = ""
    kwhView.text = ""
    if (stats != null) {
      if (stats.rain != null) {
        rainView.text = formatter.formatRain(stats.rain)
        rainCaptionView.visibility = View.VISIBLE
      }
      minView.text = formatter.formatTemperature(stats.minTemperature)
      maxView.text = formatter.formatTemperature(stats.maxTemperature)
      if (stats.kwh != null) {
        kwhView.text = formatter.formatKwh(stats.kwh)
        kwhCaptionView.visibility = View.VISIBLE
      }
    }
  }

  fun configureSymbols(useDarkSymbols: Boolean) {
    if (useDarkSymbols) {
      imageCollapse = ContextCompat.getDrawable(currentContext, R.drawable.ic_action_collapse_dark)
      imageExpand = ContextCompat.getDrawable(currentContext, R.drawable.ic_action_expand_dark)

      val imageForecastDark = ContextCompat.getDrawable(currentContext, R.drawable.ic_action_forecast_dark)
      forecastButton!!.setImageDrawable(imageForecastDark)
      val imageDiagramDark = ContextCompat.getDrawable(currentContext, R.drawable.ic_action_picture_dark)
      diagramButton!!.setImageDrawable(imageDiagramDark)
    }
    isExpanded = isExpanded
  }

  fun setTextColor(color: Int) {
    captionView.setTextColor(color)
  }

  fun highlight(highlight: Boolean) {
    setBackgroundColor(if (highlight) ColorUtil.highlight() else Color.TRANSPARENT)
  }
}
