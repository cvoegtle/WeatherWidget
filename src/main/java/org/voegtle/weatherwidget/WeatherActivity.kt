package org.voegtle.weatherwidget

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.activity_weather.*
import org.voegtle.weatherwidget.base.ThemedActivity
import org.voegtle.weatherwidget.base.UpdatingScrollView
import org.voegtle.weatherwidget.diagram.*
import org.voegtle.weatherwidget.location.LocationIdentifier
import org.voegtle.weatherwidget.location.LocationView
import org.voegtle.weatherwidget.location.WeatherLocation
import org.voegtle.weatherwidget.preferences.*
import org.voegtle.weatherwidget.util.StatisticsUpdater
import org.voegtle.weatherwidget.util.WeatherDataUpdater
import java.util.*


class WeatherActivity : ThemedActivity(), SharedPreferences.OnSharedPreferenceChangeListener {
  private var updater: WeatherDataUpdater? = null
  private var statisticsUpdater: StatisticsUpdater? = null
  private var configuration: ApplicationSettings? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setContentView(R.layout.activity_weather)
    button_google_docs.setOnClickListener {
      val browserIntent = Intent(Intent.ACTION_VIEW,
          Uri.parse("https://docs.google.com/spreadsheets/d/1ahkm9SDTqjYcsLgKIH5yjmqlAh6dKxgfIrZA5Dt9L3o/edit?usp=sharing"))
      startActivity(browserIntent)
    }

    val preferences = PreferenceManager.getDefaultSharedPreferences(this)
    readConfiguration(preferences)
    preferences.registerOnSharedPreferenceChangeListener(this)

    statisticsUpdater = StatisticsUpdater(this)

    setupLocations()
    configureLocationSymbolColor()

    updater = WeatherDataUpdater(this, configuration)

    scroll_view.register(object : UpdatingScrollView.Updater {
      override fun update() {
        updater?.updateWeatherOnce(true)
      }
    })

  }

  private fun startWeatherUpdater() {
    updater?.startWeatherScheduler(180)
  }

  private fun setupLocations() {
    for (location in configuration!!.locations) {
      addClickHandler(location)
      updateVisibility(location)
      updateState(location)
      updateTextSize(location, configuration!!.appTextSize)
    }
  }

  private fun readConfiguration(preferences: SharedPreferences) {
    val weatherSettingsReader = WeatherSettingsReader(this.applicationContext)
    configuration = weatherSettingsReader.read(preferences)
  }

  private fun addClickHandler(location: WeatherLocation) {
    val locationView = findViewById(location.weatherViewId) as LocationView
    locationView.setOnClickListener {
      if (locationView.isExpanded) {
        statisticsUpdater!!.updateStatistics(locationView, location)
      } else {
        statisticsUpdater!!.clearState(locationView)
      }
    }

    locationView.setDiagramsOnClickListener(object : View.OnClickListener {
      override fun onClick(view: View) {
        when (view.id) {
          R.id.weather_paderborn -> startActivity(Intent(this@WeatherActivity, PaderbornDiagramActivity::class.java))
          R.id.weather_bali -> startActivity(Intent(this@WeatherActivity, BaliDiagramActivity::class.java))
          R.id.weather_bonn -> startActivity(Intent(this@WeatherActivity, BonnDiagramActivity::class.java))
          R.id.weather_freiburg -> startActivity(Intent(this@WeatherActivity, FreiburgDiagramActivity::class.java))
          R.id.weather_leo -> startActivity(Intent(this@WeatherActivity, LeoDiagramActivity::class.java))
          R.id.weather_herzogenaurach -> startActivity(Intent(this@WeatherActivity, HerzoDiagramActivity::class.java))
          R.id.weather_magdeburg -> startActivity(Intent(this@WeatherActivity, MagdeburgDiagramActivity::class.java))
          R.id.weather_shenzhen -> startActivity(Intent(this@WeatherActivity, ShenzhenDiagramActivity::class.java))
          R.id.weather_mobil -> {
            val intent = Intent(this@WeatherActivity, MobilDiagramActivity::class.java)
            val location = configuration!!.findLocation(LocationIdentifier.Mobil)
            if (location != null) {
              intent.putExtra(MobilDiagramActivity::class.java.name, location.name)
            }
            startActivity(intent)
          }
        }
      }
    })


    locationView.setForecastOnClickListener(object : View.OnClickListener {
      override fun onClick(view: View) {
        val browserIntent = Intent(Intent.ACTION_VIEW, location.forecastUrl)
        startActivity(browserIntent)
      }
    })

  }

  private fun updateStatistics() {
    val updateCandidates = HashMap<LocationView, WeatherLocation>()
    for (location in configuration!!.locations) {
      val locationView = findViewById(location.weatherViewId) as LocationView
      if (locationView.isExpanded) {
        updateCandidates.put(locationView, location)
      }
    }
    statisticsUpdater!!.updateStatistics(updateCandidates, false)
  }

  private fun updateVisibility(location: WeatherLocation) {
    val show = location.preferences.showInApp
    updateVisibility(location.weatherViewId, show)
  }

  private fun updateVisibility(viewId: Int, isVisible: Boolean) {
    val view = findViewById(viewId)
    view.visibility = if (isVisible) View.VISIBLE else View.GONE
  }

  private fun updateTextSize(location: WeatherLocation, textSize: Int?) {
    if (location.preferences.showInApp) {
      val view = findViewById(location.weatherViewId) as LocationView
      view.setTextSize(textSize!!)
    }
  }

  private fun configureLocationSymbolColor() {
    // Dunkle Symbole wenn der Hintergrund hell ist
    val darkSymbols = colorScheme == ColorScheme.light
    for (location in configuration!!.locations) {
      val locationView = findViewById(location.weatherViewId) as LocationView
      locationView.configureSymbols(darkSymbols)
    }

  }

  private fun updateState(location: WeatherLocation) {
    val locationView = findViewById(location.weatherViewId) as LocationView
    statisticsUpdater!!.setupStatistics(locationView)
  }

  override fun onResume() {
    super.onResume()
    updateStatistics()
    updater!!.updateWeatherOnce(false)
    startWeatherUpdater()
  }

  override fun onPause() {
    super.onPause()
    updater!!.stopWeatherScheduler()
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    val inflater = menuInflater
    inflater.inflate(R.menu.weather_activity_menu, menu)
    return super.onCreateOptionsMenu(menu)

  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    when (item.itemId) {
      R.id.action_reload -> {
        updater!!.updateWeatherOnce(true)
        return true
      }
      R.id.action_diagrams -> {
        startActivity(Intent(this, MainDiagramActivity::class.java))
        return true
      }
      R.id.action_sort -> {
        val orderCriteriaDialog = OrderCriteriaDialogBuilder.createOrderCriteriaDialog(this, updater!!)
        orderCriteriaDialog.show()
        return true
      }
      R.id.action_perferences -> {
        startActivity(Intent(this, WeatherPreferences::class.java))
        return true
      }
    }
    return false
  }

  override fun onSharedPreferenceChanged(preferences: SharedPreferences, s: String) {
    readConfiguration(preferences)
    setupLocations()
    updater!!.stopWeatherScheduler()
    updater = WeatherDataUpdater(this, configuration)
  }
}
