package org.voegtle.weatherwidget

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.Menu
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.activity_weather.*
import org.voegtle.weatherwidget.base.ThemedActivity
import org.voegtle.weatherwidget.base.UpdatingScrollView
import org.voegtle.weatherwidget.diagram.*
import org.voegtle.weatherwidget.location.LocationIdentifier
import org.voegtle.weatherwidget.location.LocationOrderStore
import org.voegtle.weatherwidget.location.LocationView
import org.voegtle.weatherwidget.location.WeatherLocation
import org.voegtle.weatherwidget.preferences.*
import org.voegtle.weatherwidget.util.ActivityUpdateTask
import org.voegtle.weatherwidget.util.StatisticsUpdater
import org.voegtle.weatherwidget.util.UserFeedback
import java.util.*


class WeatherActivity : ThemedActivity(), SharedPreferences.OnSharedPreferenceChangeListener {
  companion object {
    val ANDROID8 = "org.voegtle.weatherwidget.Android8"
  }

  private var statisticsUpdater: StatisticsUpdater? = null
  private var configuration: ApplicationSettings? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setContentView(R.layout.activity_weather)
    button_google_docs.setOnClickListener {
      val browserIntent = Intent(Intent.ACTION_VIEW,
                                 Uri.parse(
                                     "https://docs.google.com/spreadsheets/d/1ahkm9SDTqjYcsLgKIH5yjmqlAh6dKxgfIrZA5Dt9L3o/edit?usp=sharing"))
      startActivity(browserIntent)
    }

    val preferences = PreferenceManager.getDefaultSharedPreferences(this)
    readConfiguration(preferences)
    preferences.registerOnSharedPreferenceChangeListener(this)

    statisticsUpdater = StatisticsUpdater(this)

    setupLocations()
    configureLocationSymbolColor()


    scroll_view.register(object : UpdatingScrollView.Updater {
      override fun update() {
        updateWeatherOnce(true)
      }
    })

    if (intent.action == ANDROID8) {
      showAndroid8Explanation()
    }
  }

  private fun showAndroid8Explanation() {
    val builder = AlertDialog.Builder(this)
    builder.setMessage(R.string.android8_explanation).setTitle(R.string.android8_caption)
    val dialog = builder.create()
    dialog.show()
  }

  private fun setupLocations() {
    configuration?.let {
      it.locations.forEach { location ->
        addClickHandler(location)
        updateVisibility(location)
        updateState(location)
        updateTextSize(location, it.appTextSize)
      }
    }
  }

  private fun readConfiguration(preferences: SharedPreferences) {
    val weatherSettingsReader = WeatherSettingsReader(this.applicationContext)
    configuration = weatherSettingsReader.read(preferences)
    requestLocationPermission()
  }

  private fun addClickHandler(location: WeatherLocation) {
    val locationView: LocationView = findViewById(location.weatherViewId)
    locationView.setOnClickListener {
      if (locationView.isExpanded) {
        statisticsUpdater!!.updateStatistics(locationView, location)
      } else {
        statisticsUpdater!!.clearState(locationView)
      }
    }

    locationView.diagramListener = View.OnClickListener { view ->
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
          val mobileLocation = configuration!!.findLocation(LocationIdentifier.Mobil)
          if (mobileLocation != null) {
            intent.putExtra(MobilDiagramActivity::class.java.name, mobileLocation.name)
          }
          startActivity(intent)
        }
      }
    }

    locationView.forecastListener = View.OnClickListener {
      val browserIntent = Intent(Intent.ACTION_VIEW, location.forecastUrl)
      startActivity(browserIntent)
    }
  }

  private fun updateStatistics() {
    val updateCandidates = HashMap<LocationView, WeatherLocation>()
    configuration?.let {
      it.locations.forEach { location ->
        val locationView: LocationView = findViewById(location.weatherViewId)
        if (locationView.isExpanded) {
          updateCandidates.put(locationView, location)
        }
      }
    }
    statisticsUpdater?.updateStatistics(updateCandidates, false)
  }

  private fun updateVisibility(location: WeatherLocation) {
    val show = location.preferences.showInApp
    updateVisibility(location.weatherViewId, show)
  }

  private fun updateVisibility(viewId: Int, isVisible: Boolean) {
    val view: View = findViewById(viewId)
    view.visibility = if (isVisible) View.VISIBLE else View.GONE
  }

  private fun updateTextSize(location: WeatherLocation, textSize: Int) {
    if (location.preferences.showInApp) {
      val view: LocationView = findViewById(location.weatherViewId)
      view.setTextSize(textSize)
    }
  }

  private fun configureLocationSymbolColor() {
    // Dunkle Symbole wenn der Hintergrund hell ist
    val darkSymbols = colorScheme == ColorScheme.light
    configuration?.let {
      it.locations.forEach {
        val view: LocationView = findViewById(it.weatherViewId)
        view.configureSymbols(darkSymbols)
      }
    }

  }

  private fun updateState(location: WeatherLocation) {
    val locationView: LocationView = findViewById(location.weatherViewId)
    statisticsUpdater!!.setupStatistics(locationView)
  }

  override fun onResume() {
    super.onResume()
    updateStatistics()
    updateWeatherOnce(false)
  }

  override fun onPause() {
    super.onPause()
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    val inflater = menuInflater
    inflater.inflate(R.menu.weather_activity_menu, menu)
    return super.onCreateOptionsMenu(menu)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean =
      when (item.itemId) {
        R.id.action_reload -> {
          updateWeatherOnce(true)
          true
        }
        R.id.action_diagrams -> {
          startActivity(Intent(this, MainDiagramActivity::class.java))
          true
        }
        R.id.action_sort -> {
          val orderCriteriaDialog = OrderCriteriaDialogBuilder.createOrderCriteriaDialog(this)
          orderCriteriaDialog.show()
          true
        }
        R.id.action_perferences -> {
          startActivity(Intent(this, WeatherPreferences::class.java))
          true
        }
        else -> false
      }

  override fun onSharedPreferenceChanged(preferences: SharedPreferences, s: String) {
    readConfiguration(preferences)
    setupLocations()
    updateWeatherOnce(true)
  }

  fun requestLocationPermission() {
    val locationOrderStore = LocationOrderStore(applicationContext)
    val orderCriteria = locationOrderStore.readOrderCriteria()
    if (orderCriteria == OrderCriteria.location) {
      val permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
      if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 0)
      }
    }
  }

  override fun onRequestPermissionsResult(requestId: Int, permissions: Array<String>, grantResults: IntArray) {
    if (grantResults.isNotEmpty()) {
      if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
        UserFeedback(this).showMessage(R.string.message_location_permission_required, true)
        val locationOrderStore = LocationOrderStore(applicationContext)
        locationOrderStore.writeOrderCriteria(OrderCriteria.default)
        updateWeatherOnce(false)
      }
    }
  }

  fun updateWeatherOnce(showToast: Boolean) {
    ActivityUpdateTask(this, configuration!!, showToast).execute()
  }


}
