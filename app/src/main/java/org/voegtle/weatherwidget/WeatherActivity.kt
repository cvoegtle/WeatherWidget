package org.voegtle.weatherwidget

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity // Import geändert/hinzugefügt
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.google.gson.Gson
// import org.voegtle.weatherwidget.base.ThemedActivity // Entfernt
import org.voegtle.weatherwidget.data.WeatherData
import org.voegtle.weatherwidget.databinding.ActivityWeatherBinding
import org.voegtle.weatherwidget.diagram.BaliDiagramActivity
import org.voegtle.weatherwidget.diagram.BonnDiagramActivity
import org.voegtle.weatherwidget.diagram.FreiburgDiagramActivity
import org.voegtle.weatherwidget.diagram.HerzoDiagramActivity
import org.voegtle.weatherwidget.diagram.LeoDiagramActivity
import org.voegtle.weatherwidget.diagram.MagdeburgDiagramActivity
import org.voegtle.weatherwidget.diagram.MainDiagramActivity
import org.voegtle.weatherwidget.diagram.MobilDiagramActivity
import org.voegtle.weatherwidget.diagram.PaderbornDiagramActivity
import org.voegtle.weatherwidget.diagram.ShenzhenDiagramActivity
import org.voegtle.weatherwidget.location.LocationContainer
import org.voegtle.weatherwidget.location.LocationIdentifier
import org.voegtle.weatherwidget.location.LocationOrderStore
import org.voegtle.weatherwidget.location.LocationView
import org.voegtle.weatherwidget.location.UserLocationUpdater
import org.voegtle.weatherwidget.notification.NotificationSystemManager
import org.voegtle.weatherwidget.preferences.ApplicationSettings
import org.voegtle.weatherwidget.preferences.NotificationSettings
import org.voegtle.weatherwidget.preferences.OrderCriteria
import org.voegtle.weatherwidget.preferences.OrderCriteriaDialogBuilder
import org.voegtle.weatherwidget.preferences.WeatherPreferences
import org.voegtle.weatherwidget.preferences.WeatherSettingsReader
import org.voegtle.weatherwidget.util.ActivityUpdateWorker
import org.voegtle.weatherwidget.util.DataFormatter
import org.voegtle.weatherwidget.util.FetchAllResponse
import org.voegtle.weatherwidget.util.StatisticsUpdater
import org.voegtle.weatherwidget.util.UserFeedback
import org.voegtle.weatherwidget.widget.ScreenPainterFactory

class WeatherActivity : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener { // Basisklasse geändert
    private var statisticsUpdater: StatisticsUpdater? = null
    private var configuration: ApplicationSettings? = null

    private lateinit var binding: ActivityWeatherBinding
    private var locationOrderStore: LocationOrderStore? = null
    private val formatter = DataFormatter()

    // wird benötigt um die Daten asynchron zu aktualisieren
    private var workInfo: LiveData<MutableList<WorkInfo?>>? = null
    private var userLocationUpdater: UserLocationUpdater? = null

    // Aus ThemedActivity übernommen
    private fun configureTheme() {
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        val weatherSettingsReader = WeatherSettingsReader(this.applicationContext)
        val configuration = weatherSettingsReader.read(preferences)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        configureTheme() // Theme-Logik vor super.onCreate() aufrufen
        super.onCreate(savedInstanceState)
        binding = ActivityWeatherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar: com.google.android.material.appbar.MaterialToolbar = binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.app_name)

        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        readConfiguration(preferences) // Dies wird das colorScheme erneut setzen, was ok ist.
        preferences.registerOnSharedPreferenceChangeListener(this)

        locationOrderStore = LocationOrderStore(this.applicationContext)
        statisticsUpdater = StatisticsUpdater(this)
        userLocationUpdater = UserLocationUpdater(this)
    }

    // Removed duplicate startActivity method, super.startActivity is sufficient if no custom logic is needed.
    // override fun startActivity(intent: Intent?) {
    //     super.startActivity(intent)
    // }

    private fun readConfiguration(preferences: SharedPreferences) {
        val weatherSettingsReader = WeatherSettingsReader(this.applicationContext)
        configuration = weatherSettingsReader.read(preferences)
        requestPermissions()
        enableNotificationsIfPermitted()
    }

    private fun updateVisibility(viewId: Int, isVisible: Boolean) {
        val view: View = findViewById(viewId)
        view.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    override fun onResume() {
        super.onResume()
        updateWeatherOnce(false)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.weather_activity_menu, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
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

    override fun onSharedPreferenceChanged(preferences: SharedPreferences, s: String?) {
        readConfiguration(preferences) // Dies wird das colorScheme aktualisieren
        updateWeatherOnce(true)
    }

    fun requestPermissions() {
        val missingPermissions = ArrayList<String>()
        val locationOrderStore = LocationOrderStore(applicationContext)
        val orderCriteria = locationOrderStore.readOrderCriteria()
        if (orderCriteria == OrderCriteria.location) {
            checkForPermissionsToRequest(Manifest.permission.ACCESS_FINE_LOCATION)?.let { missingPermissions.add(it) }
        }

        if (NotificationSettings(this).isEnabled() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            checkForPermissionsToRequest(Manifest.permission.POST_NOTIFICATIONS)?.let { missingPermissions.add(it) }
        }

        if (!missingPermissions.isEmpty()) {
            ActivityCompat.requestPermissions(this, missingPermissions.toTypedArray(), 0)
        }
    }

    private fun checkForPermissionsToRequest(permission: String): String? {
        val permissionCheck = ContextCompat.checkSelfPermission(this, permission)
        return if (permissionCheck != PackageManager.PERMISSION_GRANTED) permission else null
    }

    override fun onRequestPermissionsResult(requestId: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestId, permissions, grantResults)
        for ((index, permission) in permissions.withIndex()) {
            val userGrantResponse = grantResults[index]
            if (permission == Manifest.permission.ACCESS_FINE_LOCATION && userGrantResponse != PackageManager.PERMISSION_GRANTED) {
                rollbackLocationOrdering()
            }
            if (permission == Manifest.permission.POST_NOTIFICATIONS && userGrantResponse != PackageManager.PERMISSION_GRANTED) {
                disableNotifications()
            }
        }
    }

    private fun enableNotificationsIfPermitted() {
        if (isNotificationsPermitted()) {
            enableNotifications()
        }
    }

    private fun isNotificationsPermitted(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            return permissionCheck == PackageManager.PERMISSION_GRANTED
        } else {
            return true
        }
    }

    private fun disableNotifications() {
        NotificationSettings(this).saveEnabled(false)
    }

    private fun enableNotifications() {
        NotificationSettings(this).saveEnabled(true)
    }

    private fun rollbackLocationOrdering() {
        UserFeedback(this).showMessage(R.string.message_location_permission_required, true)
        val locationOrderStore = LocationOrderStore(applicationContext)
        locationOrderStore.writeOrderCriteria(OrderCriteria.default)
        updateWeatherOnce(false)
    }

    fun updateWeatherOnce(showToast: Boolean) {
        userLocationUpdater!!.updateLocation()

        val activityUpdateRequest = OneTimeWorkRequestBuilder<ActivityUpdateWorker>().addTag(ActivityUpdateWorker.WEATHER_DATA).build()
        val workManager = WorkManager.getInstance(applicationContext)
        workManager.enqueue(activityUpdateRequest)
        val workInfoByIdLiveData = workManager.getWorkInfoByIdLiveData(activityUpdateRequest.id)

        val observer = Observer<WorkInfo?>() { latestWorkInfo ->
            if (latestWorkInfo != null && latestWorkInfo.state.isFinished) {
                val weatherDataJson = latestWorkInfo.outputData.getString(ActivityUpdateWorker.WEATHER_DATA)
                val weatherData = Gson().fromJson(weatherDataJson, FetchAllResponse::class.java)
                updateActivity(weatherData, showToast)
            }
        }
        workInfoByIdLiveData.observe(this, observer)
    }

    private fun updateActivity(weatherData: FetchAllResponse, showToast: Boolean) {
        try {
            updateLocations(weatherData.weatherMap)
            updateWidgets(weatherData.weatherMap)

            UserFeedback(applicationContext).showMessage(
                if (weatherData.valid) R.string.message_data_updated else R.string.message_data_update_failed, showToast
            )

            val notificationManager = NotificationSystemManager(applicationContext, configuration!!)
            notificationManager.updateNotification(weatherData)
        } catch (th: Throwable) {
            UserFeedback(applicationContext).showMessage(R.string.message_data_update_failed, true)
            Log.e(WeatherActivity::class.java.toString(), "Failed to update View", th)
        }

    }

    private fun updateView(view: LocationView, caption: String, data: WeatherData, color: Int) {
        view.setCaption(caption)
        view.setData(data)
        view.setTextColor(color)
    }

    private fun updateWidgets(data: HashMap<LocationIdentifier, WeatherData>) {
        val factory = ScreenPainterFactory(this, configuration!!)
        val screenPainters = factory.createScreenPainters()
        for (screenPainter in screenPainters) {
            screenPainter.updateWidgetData(data)
            screenPainter.showDataIsValid()
        }
    }

    private fun updateLocations(data: HashMap<LocationIdentifier, WeatherData>) {
        val container = locationContainer()
        val locationContainer = LocationContainer(applicationContext, container)
        locationContainer.showWeatherData(configuration!!.locations, data, onDiagramClick = (::onDiagramClicked), onForecastClick = (::onForecastClicked))
    }

    private fun onDiagramClicked(locationIdentifier: LocationIdentifier) {
        navigateToDiagramActivity(locationIdentifier)
    }

    private fun navigateToDiagramActivity(locationIdentifier: LocationIdentifier) {
        val intent = mapLocation2Intent(locationIdentifier)
        startActivity(intent)
    }

    private fun mapLocation2Intent(locationIdentifier: LocationIdentifier): Intent {
        val intent = when (locationIdentifier) {
            LocationIdentifier.Paderborn -> Intent(this@WeatherActivity, PaderbornDiagramActivity::class.java)
            LocationIdentifier.BadLippspringe -> Intent(this@WeatherActivity, BaliDiagramActivity::class.java)
            LocationIdentifier.Bonn -> Intent(this@WeatherActivity, BonnDiagramActivity::class.java)
            LocationIdentifier.Freiburg -> Intent(this@WeatherActivity, FreiburgDiagramActivity::class.java)
            LocationIdentifier.Leopoldshoehe -> Intent(this@WeatherActivity, LeoDiagramActivity::class.java)
            LocationIdentifier.Herzogenaurach -> Intent(this@WeatherActivity, HerzoDiagramActivity::class.java)
            LocationIdentifier.Magdeburg -> Intent(this@WeatherActivity, MagdeburgDiagramActivity::class.java)
            LocationIdentifier.Shenzhen -> Intent(this@WeatherActivity, ShenzhenDiagramActivity::class.java)
            LocationIdentifier.Mobil -> Intent(this@WeatherActivity, MobilDiagramActivity::class.java)
        }
        return intent
    }

    private fun onForecastClicked(forecastUrl: Uri) {
        val browserIntent = Intent(Intent.ACTION_VIEW, forecastUrl)
        startActivity(browserIntent)
    }

    private fun locationContainer() = binding.locationContainer

}
