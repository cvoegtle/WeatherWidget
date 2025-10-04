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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import kotlinx.coroutines.launch
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
import org.voegtle.weatherwidget.location.UserLocationUpdater
import org.voegtle.weatherwidget.notification.NotificationSystemManager
import org.voegtle.weatherwidget.preferences.ApplicationPreferences
import org.voegtle.weatherwidget.preferences.NotificationSettings
import org.voegtle.weatherwidget.preferences.OrderCriteria
import org.voegtle.weatherwidget.preferences.OrderCriteriaDialogBuilder
import org.voegtle.weatherwidget.preferences.WeatherPreferences
import org.voegtle.weatherwidget.preferences.WeatherPreferencesReader
import org.voegtle.weatherwidget.cache.StateCache
import org.voegtle.weatherwidget.cache.WeatherDataCache
import org.voegtle.weatherwidget.location.LocationDataSet
import org.voegtle.weatherwidget.location.LocationDataSetFactory
import org.voegtle.weatherwidget.location.LocationSorter
import org.voegtle.weatherwidget.util.DateUtil
import org.voegtle.weatherwidget.util.WeatherDataUpdateWorker
import org.voegtle.weatherwidget.util.FetchAllResponse
import org.voegtle.weatherwidget.util.StatisticUpdateWorker
import org.voegtle.weatherwidget.util.UserFeedback
import org.voegtle.weatherwidget.widget.updateWeatherWidgetState
import java.util.Date

class WeatherActivity : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener {
    private var configuration: ApplicationPreferences? = null

    private lateinit var binding: ActivityWeatherBinding
    private var locationOrderStore: LocationOrderStore? = null

    private var userLocationUpdater: UserLocationUpdater? = null
    private var stateCache: StateCache? = null
    private var weatherDataCache: WeatherDataCache? = null
    private var locationDataSetFactory: LocationDataSetFactory? = null
    private var locationSorter: LocationSorter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWeatherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar: com.google.android.material.appbar.MaterialToolbar = binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.app_name)

        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        readConfiguration(preferences)
        preferences.registerOnSharedPreferenceChangeListener(this)

        locationOrderStore = LocationOrderStore(this.applicationContext)
        userLocationUpdater = UserLocationUpdater(this)
        stateCache = StateCache(this)
        weatherDataCache = WeatherDataCache(this)
        locationDataSetFactory = LocationDataSetFactory(this)
        locationSorter = LocationSorter(this)
    }

    private fun readConfiguration(preferences: SharedPreferences) {
        val weatherPreferencesReader = WeatherPreferencesReader(this.applicationContext)
        configuration = weatherPreferencesReader.read(preferences)
        requestPermissions()
        enableNotificationsIfPermitted()
    }

    override fun onResume() {
        super.onResume()
        updateAll(false)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.weather_activity_menu, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.action_reload -> {
            updateAll(true)
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

        R.id.action_preferences -> {
            startActivity(Intent(this, WeatherPreferences::class.java))
            true
        }

        else -> false
    }

    override fun onSharedPreferenceChanged(preferences: SharedPreferences, s: String?) {
        readConfiguration(preferences)
        updateAll(true)
    }

    private var timeOfLastUpdate: Date = DateUtil.yesterday
    fun updateAll(showToast: Boolean) {
        if (DateUtil.isMinimumTimeSinceLastUpdate(timeOfLastUpdate)) {
            timeOfLastUpdate = Date()
            updateWeather(showToast)
            updateStatistics(false)
        }
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
        updateAll(false)
    }

    fun updateWeather(showToast: Boolean) {
        userLocationUpdater!!.updateLocation()

        val weatherDataUpdateRequest = OneTimeWorkRequestBuilder<WeatherDataUpdateWorker>().addTag(WeatherDataUpdateWorker.WEATHER_DATA).build()
        val workManager = WorkManager.getInstance(applicationContext)
        workManager.enqueue(weatherDataUpdateRequest)
        val workInfoByIdLiveData = workManager.getWorkInfoByIdLiveData(weatherDataUpdateRequest.id)

        val observer = Observer<WorkInfo?>() { latestWorkInfo ->
            if (latestWorkInfo != null && latestWorkInfo.state.isFinished) {
                updateActivity(showToast)
            }
        }
        workInfoByIdLiveData.observe(this, observer)
    }

    fun updateStatistics(showToast: Boolean) {
        val statisticsUpdateRequest = OneTimeWorkRequestBuilder<StatisticUpdateWorker>().addTag(StatisticUpdateWorker.STATISTIC_DATA).build()
        val workManager = WorkManager.getInstance(applicationContext)
        workManager.enqueue(statisticsUpdateRequest)
        val workInfoByIdLiveData = workManager.getWorkInfoByIdLiveData(statisticsUpdateRequest.id)

        val observer = Observer<WorkInfo?>() { latestWorkInfo ->
            if (latestWorkInfo != null && latestWorkInfo.state.isFinished) {
                updateActivity(showToast)
            }
        }
        workInfoByIdLiveData.observe(this, observer)

    }

    private fun updateActivity(showToast: Boolean) {
        try {
            val weatherData = weatherDataCache!!.read()
            weatherData?.let {
                val locationDataSets = locationDataSetFactory!!.assembleLocationDataSets(configuration!!.locations, it.weatherMap)
                locationSorter!!.sort(locationDataSets)

                updateLocations(locationDataSets)
                updateWidgets(locationDataSets)

                showUserToast(it, showToast)
                updateNotification(it)
            }
        } catch (th: Throwable) {
            UserFeedback(applicationContext).showMessage(R.string.message_data_update_failed, true)
            Log.e(WeatherActivity::class.java.toString(), "Failed to update View", th)
        }

    }

    private fun updateWidgets(locationDataSets: List<LocationDataSet>) {
        // Neue Logik, um das Glance-Widget zu aktualisieren
        lifecycleScope.launch {
            updateWeatherWidgetState(applicationContext, locationDataSets)
        }
    }

    private fun updateLocations(locationDataSets: List<LocationDataSet>) {
        val container = locationContainer()
        val locationContainer = LocationContainer(applicationContext, container)
        locationContainer.showWeatherData(locationDataSets,
            onDiagramClick = (::onDiagramClicked),
            onForecastClick = (::onForecastClicked),
            onExpandStateChanged = (::onExpandedClicked),
            onPullToRefresh = (::onPullToRefresh),
            onDataMiningButtonClick = (::onDataMiningButtonClick))
    }

    private fun showUserToast(response: FetchAllResponse, showToast: Boolean) {
        UserFeedback(applicationContext).showMessage(
            if (response.valid) R.string.message_data_updated else R.string.message_data_update_failed,
            showToast
        )
    }

    private fun updateNotification(fetchAllResponse: FetchAllResponse) {
        val notificationManager = NotificationSystemManager(applicationContext, configuration!!)
        notificationManager.updateNotification(fetchAllResponse)
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

    private fun onExpandedClicked(locationIdentifier: LocationIdentifier, isExpanded: Boolean) {
        val state = stateCache!!.read(locationIdentifier)
        state.isExpanded = isExpanded
        state.makeOutdated()
        stateCache!!.save(state)

        if (isExpanded) {
            updateStatistics(false)
        } else {
            updateActivity(false)
        }
    }

    private fun onPullToRefresh(overscrollAmount: Float) {
        if (overscrollAmount > 40) {
            updateAll(true)
        }
    }

    private fun onDataMiningButtonClick() {
        val browserIntent = Intent(
            Intent.ACTION_VIEW, Uri.parse(
                "https://docs.google.com/spreadsheets/d/1ahkm9SDTqjYcsLgKIH5yjmqlAh6dKxgfIrZA5Dt9L3o/edit?usp=sharing")
        )
        startActivity(browserIntent)
    }

    private fun locationContainer() = binding.locationContainer

}
