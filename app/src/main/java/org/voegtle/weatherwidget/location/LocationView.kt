package org.voegtle.weatherwidget.location

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import org.voegtle.weatherwidget.R
import org.voegtle.weatherwidget.data.Statistics
import org.voegtle.weatherwidget.data.StatisticsSet
import org.voegtle.weatherwidget.data.WeatherData
import org.voegtle.weatherwidget.databinding.ViewLocationBinding
import org.voegtle.weatherwidget.util.ColorUtil
import org.voegtle.weatherwidget.util.DataFormatter

class LocationView(private val currentContext: Context, attrs: AttributeSet) : LinearLayout(currentContext, attrs) {
    private var imageExpand = ContextCompat.getDrawable(currentContext, R.drawable.ic_action_expand)
    private var imageCollapse = ContextCompat.getDrawable(currentContext, R.drawable.ic_action_collapse)
    private var externalClickListener: OnClickListener? = null
    var diagramListener: OnClickListener? = null
    var forecastListener: OnClickListener? = null

    private lateinit var binding: ViewLocationBinding


    init {
        val li = currentContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = ViewLocationBinding.inflate(li, this, true)

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
        binding.resizeButton.setImageDrawable(if (isExpanded) imageCollapse else imageExpand)
        binding.composeViewStatistics.visibility = if (isExpanded) View.VISIBLE else View.GONE
    }

    private fun initializeButtons(attributes: TypedArray) {
        binding.resizeButton.setOnClickListener { arg0 ->
            isExpanded = !isExpanded
            externalClickListener?.onClick(arg0)
        }
        repaintExpandButton()

        val diagrams = attributes.getBoolean(R.styleable.LocationView_diagrams, false)
        binding.diagramButton.visibility = if (diagrams) View.VISIBLE else View.GONE
        binding.diagramButton.setOnClickListener {
            diagramListener?.onClick(this@LocationView)
        }

        val forecast = attributes.getBoolean(R.styleable.LocationView_forecast, true)
        binding.forecastButton.visibility = if (forecast) View.VISIBLE else View.GONE
        binding.forecastButton.setOnClickListener { forecastListener?.onClick(this@LocationView) }
    }

    override fun setOnClickListener(l: OnClickListener?) {
        externalClickListener = l
    }

    private fun initializeTextViews(attributes: TypedArray) {
        binding.caption.text = attributes.getString(R.styleable.LocationView_caption)
    }

    fun setTextSize(textSize: Int) {
        binding.caption.textSize = (textSize + 1).toFloat()
        updateTextSize(binding.gridCurrent, textSize)
    }

    private fun updateTextSize(container: GridLayout, textSize: Int) {
        (0..container.childCount - 1)
            .map { container.getChildAt(it) as TextView }
            .forEach { it.textSize = textSize.toFloat() }
    }


    fun setCaption(captionText: String) {
        binding.caption.text = captionText
    }

    fun setData(data: WeatherData) {
        binding.temperature.text = formatter.formatTemperatureForActivity(data)
        binding.humidity.text = formatter.formatHumidityForActivity(data)

        setBarometer(data.barometer)
        setSolarradiation(data.solarradiation)
        setPowerProduction(data.powerProduction)
        setPowerFeed(data.powerFeed)
        setUVIndex(data.UV)

        setRainData(data.rain, binding.labelRainLastHour, binding.rainLastHour)
        setRainData(data.rainToday, binding.labelRainToday, binding.rainToday)
        setWind(data.wind)
        setWindGust(data.windgust);
        setSolarData(data.watt)
    }

    private fun setBarometer(value: Float?) {
        if (value != null && value > 0.0) {
            show(binding.labelBarometer, binding.barometer)
            binding.barometer.text = formatter.formatBarometer(value)
        } else {
            hide(binding.labelBarometer, binding.barometer)
        }
    }

    private fun setSolarradiation(value: Float?) {
        if (value != null && value > 0.0) {
            show(binding.labelSolarradiation, binding.solarradiation)
            binding.solarradiation.text = formatter.formatSolarradiation(value)
        } else {
            hide(binding.labelSolarradiation, binding.solarradiation)
        }
    }

    private fun setPowerProduction(powerProduction: Float?) {
        if (powerProduction != null && powerProduction >= 1.0) {
            show(binding.labelPowerProduction, binding.powerProduction)
            binding.powerProduction.text = formatter.formatWatt(powerProduction)
        } else {
            hide(binding.labelPowerProduction, binding.powerProduction)
        }
    }

    private fun setPowerFeed(powerFeed: Float?) {
        if (powerFeed != null && powerFeed >= 5.0) {
            show(binding.labelPowerFeed, binding.powerFeed)
            binding.powerFeed.text = formatter.formatWatt(powerFeed)
        } else {
            hide(binding.labelPowerFeed, binding.powerFeed)
        }
    }

    private fun setUVIndex(value: Float?) {
        if (value != null && value > 0.0) {
            show(binding.labelUv, binding.uv)
            binding.uv.text = formatter.formatInteger(value)
        } else {
            hide(binding.labelUv, binding.uv)
        }
    }

    private fun setSolarData(watt: Float?) {
        if (watt != null && watt > 0.0) {
            show(binding.labelSolarOutput, binding.solarOutput)
            binding.solarOutput.text = formatter.formatWatt(watt)
        } else {
            hide(binding.labelSolarOutput, binding.solarOutput)
        }
    }

    private fun setRainData(value: Float?, rainLabel: TextView, rain: TextView) {
        rainLabel.visibility = if (value != null && value > 0.0f) View.VISIBLE else View.GONE
        rain.visibility = if (value != null && value > 0.0f) View.VISIBLE else View.GONE
        rain.text = if (value != null && value > 0.0f) formatter.formatRain(value) else ""
    }

    private fun setWind(windgust: Float?) {
        if (windgust != null && windgust >= 1.0) {
            show(binding.labelWindSpeed, binding.windSpeed)
            binding.windSpeed.text = formatter.formatWind(windgust)
        } else {
            hide(binding.labelWindSpeed, binding.windSpeed)
        }
    }

    private fun setWindGust(windgust: Float?) {
        if (windgust != null && windgust >= 10.0) {
            show(binding.labelWindGust, binding.windGust)
            binding.windGust.text = formatter.formatWind(windgust)
        } else {
            hide(binding.labelWindGust, binding.windGust)
        }
    }

    fun setMoreData(statistics: Statistics) {
        binding.composeViewStatistics.setContent {
            StatisticsView(statistics)
        }
    }



    fun configureSymbols(useDarkSymbols: Boolean) {
        if (useDarkSymbols) {
            imageCollapse = ContextCompat.getDrawable(currentContext, R.drawable.ic_action_collapse_dark)
            imageExpand = ContextCompat.getDrawable(currentContext, R.drawable.ic_action_expand_dark)
            repaintExpandButton()

            val imageForecastDark = ContextCompat.getDrawable(currentContext, R.drawable.ic_action_forecast_dark)
            binding.forecastButton.setImageDrawable(imageForecastDark)
            val imageDiagramDark = ContextCompat.getDrawable(currentContext, R.drawable.ic_action_picture_dark)
            binding.diagramButton.setImageDrawable(imageDiagramDark)
        }
    }

    fun setTextColor(color: Int) {
        binding.caption.setTextColor(color)
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
