package org.voegtle.weatherwidget

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf 
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember 
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color as ComposeColor
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope 
import androidx.preference.PreferenceManager
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.google.gson.Gson
import kotlinx.coroutines.launch
import org.voegtle.weatherwidget.base.PullToRefreshContainer
import org.voegtle.weatherwidget.base.ThemedActivity
import org.voegtle.weatherwidget.data.WeatherData
import org.voegtle.weatherwidget.databinding.ActivityWeatherBinding
import org.voegtle.weatherwidget.diagram.*
import org.voegtle.weatherwidget.location.*
import org.voegtle.weatherwidget.notification.NotificationSystemManager
import org.voegtle.weatherwidget.preferences.*
import org.voegtle.weatherwidget.util.*
import org.voegtle.weatherwidget.widget.ScreenPainterFactory
import java.util.HashMap

class WeatherActivity : ThemedActivity(), SharedPreferences.OnSharedPreferenceChangeListener {
    private lateinit var statisticsUpdater: StatisticsUpdater
    private var configuration: ApplicationSettings? = null
    private lateinit var binding: ActivityWeatherBinding
    private var locationOrderStore: LocationOrderStore? = null
    private val formatter = DataFormatter()
    private var userLocationUpdater: UserLocationUpdater? = null

    private lateinit var locationContainer: LocationContainer

    private val displayedLocationOrder = mutableStateListOf<LocationIdentifier>()
    private val locationHighlightTriggers = mutableMapOf<LocationIdentifier, MutableState<Boolean>>()

    private val locationWeatherStates = mutableMapOf<LocationIdentifier, MutableState<WeatherData?>>()
    private val locationStatisticsStates = mutableMapOf<LocationIdentifier, MutableState<org.voegtle.weatherwidget.data.Statistics?>>()
    private val locationCaptionStates = mutableMapOf<LocationIdentifier, MutableState<String>>()
    private val locationColorStates = mutableMapOf<LocationIdentifier, MutableState<Int>>()
    private val locationIsFavoriteStates = mutableMapOf<LocationIdentifier, MutableState<Boolean>>()
    private val locationShowDiagramButtonStates = mutableMapOf<LocationIdentifier, MutableState<Boolean>>()
    private val locationShowForecastButtonStates = mutableMapOf<LocationIdentifier, MutableState<Boolean>>()

    // In WeatherActivity, zu den anderen States hinzufügen:
    private val isWeatherUpdating = mutableStateOf(false)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWeatherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        readConfiguration(preferences) 
        preferences.registerOnSharedPreferenceChangeListener(this)

        locationOrderStore = LocationOrderStore(this.applicationContext)
        statisticsUpdater = StatisticsUpdater(applicationContext)
        userLocationUpdater = UserLocationUpdater(this)
        locationContainer = LocationContainer(applicationContext, configuration!!)

        initializeAndSetupComposeViews()
    }

    private fun initializeStatesForLocation(location: WeatherLocation) {
        val key = location.key
        if (!locationWeatherStates.containsKey(key)) {
            locationWeatherStates[key] = mutableStateOf(null)
            locationStatisticsStates[key] = mutableStateOf(null)
            locationCaptionStates[key] = mutableStateOf(location.name) 
            locationColorStates[key] = mutableStateOf(ContextCompat.getColor(this, R.color.primary_blue))
            locationIsFavoriteStates[key] = mutableStateOf(location.preferences.favorite)
            locationShowDiagramButtonStates[key] = mutableStateOf(true) 
            locationShowForecastButtonStates[key] = mutableStateOf(location.forecastUrl.toString().isNotBlank() && location.forecastUrl != Uri.EMPTY)
            locationHighlightTriggers[key] = mutableStateOf(false) 
        }
    }


    private fun initializeAndSetupComposeViews() {
        configuration?.locations?.forEach {
            initializeStatesForLocation(it)
        }

        if (displayedLocationOrder.isEmpty() && configuration != null) {
            val initialOrder = configuration!!.locations
                .filter { it.preferences.showInApp } 
                .map { it.key }
            displayedLocationOrder.addAll(initialOrder)
        }


        binding.mainComposeView.setContent {
            MaterialTheme {
                PullToRefreshContainer(
                    isUpdating = isWeatherUpdating.value,
                    onUpdateRequested = {
                        updateWeatherOnce(true)
                    },
                    modifier = Modifier.fillMaxSize()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        displayedLocationOrder.forEach { locationIdentifier ->
                            val location = configuration?.findLocation(locationIdentifier)
                            if (location != null && location.preferences.showInApp) {
                                val weatherDataState = locationWeatherStates[locationIdentifier]
                                val statisticsState = locationStatisticsStates[locationIdentifier]
                                val captionState = locationCaptionStates[locationIdentifier]
                                val colorState = locationColorStates[locationIdentifier]
                                val isFavoriteState = locationIsFavoriteStates[locationIdentifier]
                                val showDiagramButtonState = locationShowDiagramButtonStates[locationIdentifier]
                                val showForecastButtonState = locationShowForecastButtonStates[locationIdentifier]
                                val highlightTriggerState = locationHighlightTriggers[locationIdentifier]

                                if (weatherDataState == null || statisticsState == null || captionState == null ||
                                    colorState == null || isFavoriteState == null || showDiagramButtonState == null ||
                                    showForecastButtonState == null || highlightTriggerState == null
                                ) {
                                    Log.e("WeatherActivity", "State for $locationIdentifier is null, skipping Composable.")
                                    return@forEach
                                }

                                val captionColorInt = colorState.value
                                val composeCaptionColor = remember(captionColorInt) { ComposeColor(captionColorInt) }

                                LocationViewComposable(
                                    caption = captionState.value,
                                    captionColor = composeCaptionColor,
                                    weatherData = weatherDataState.value,
                                    statistics = statisticsState.value,
                                    formatter = this@WeatherActivity.formatter,
                                    showDiagramButton = showDiagramButtonState.value,
                                    showForecastButton = showForecastButtonState.value,
                                    highlightTrigger = highlightTriggerState.value,
                                    onHighlightFinished = {
                                        if (highlightTriggerState.value) {
                                            highlightTriggerState.value = false
                                        }
                                    },
                                    onDiagramClick = {
                                        val intent = when (location.key) {
                                            LocationIdentifier.Paderborn -> Intent(this@WeatherActivity, PaderbornDiagramActivity::class.java)
                                            LocationIdentifier.BadLippspringe -> Intent(this@WeatherActivity, BaliDiagramActivity::class.java)
                                            LocationIdentifier.Bonn -> Intent(this@WeatherActivity, BonnDiagramActivity::class.java)
                                            LocationIdentifier.Freiburg -> Intent(this@WeatherActivity, FreiburgDiagramActivity::class.java)
                                            LocationIdentifier.Leopoldshoehe -> Intent(this@WeatherActivity, LeoDiagramActivity::class.java)
                                            LocationIdentifier.Herzogenaurach -> Intent(this@WeatherActivity, HerzoDiagramActivity::class.java)
                                            LocationIdentifier.Magdeburg -> Intent(this@WeatherActivity, MagdeburgDiagramActivity::class.java)
                                            LocationIdentifier.Shenzhen -> Intent(this@WeatherActivity, ShenzhenDiagramActivity::class.java)
                                            LocationIdentifier.Mobil -> {
                                                val mobileIntent = Intent(this@WeatherActivity, MobilDiagramActivity::class.java)
                                                configuration?.findLocation(LocationIdentifier.Mobil)?.let {
                                                    mobileIntent.putExtra(MobilDiagramActivity::class.java.name, it.name)
                                                }
                                                mobileIntent
                                            }

                                            else -> null
                                        }
                                        intent?.let { startActivity(it) }
                                    },
                                    onForecastClick = {
                                        if (location.forecastUrl.toString().isNotBlank() && location.forecastUrl != Uri.EMPTY) {
                                            val browserIntent = Intent(Intent.ACTION_VIEW, location.forecastUrl)
                                            startActivity(browserIntent)
                                        }
                                    },
                                    onExpandStateChanged = { isExpanded ->
                                        if (isExpanded) {
                                            Log.d("WeatherActivity", "Expanding ${location.name}, fetching statistics.")
                                            statisticsUpdater.fetchStatisticsForLocation(location, forceUpdate = false) { fetchedStatistics ->
                                                runOnUiThread {
                                                    statisticsState.value = fetchedStatistics
                                                    if (fetchedStatistics != null) {
                                                        Log.d("WeatherActivity", "Statistics updated for ${location.name}.")
                                                    } else {
                                                        Log.w("WeatherActivity", "No statistics for ${location.name}.")
                                                    }
                                                }
                                            }
                                        } else {
                                            Log.d("WeatherActivity", "Collapsing ${location.name}.")
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            if (isFavoriteState.value) ComposeColor(ColorUtil.favorite())
                                            else ComposeColor.Transparent
                                        )
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun readConfiguration(preferences: SharedPreferences) {
        val weatherSettingsReader = WeatherSettingsReader(this.applicationContext)
        configuration = weatherSettingsReader.read(preferences)

        configuration?.locations?.forEach { location ->
            initializeStatesForLocation(location) 
            locationIsFavoriteStates[location.key]?.value = location.preferences.favorite
            locationShowForecastButtonStates[location.key]?.value = location.forecastUrl.toString().isNotBlank() && location.forecastUrl != Uri.EMPTY
        }

        val currentConfig = configuration
        if (currentConfig != null) {
            val newOrder = currentConfig.locations
                .filter { it.preferences.showInApp }
                .map { it.key }
            displayedLocationOrder.clear()
            displayedLocationOrder.addAll(newOrder)
        }

        requestPermissions()
        enableNotificationsIfPermitted()
    }


    private fun updateState(location: WeatherLocation) {
        val cachedStatistics = statisticsUpdater.getCachedStatistics(location)
        if (cachedStatistics != null) {
            runOnUiThread {
                locationStatisticsStates[location.key]?.value = cachedStatistics
                Log.d("WeatherActivity", "Cached stats for ${location.name}.")
            }
        } else {
            Log.d("WeatherActivity", "No cached stats for ${location.name}.")
        }
    }

    override fun onResume() {
        super.onResume()
        updateWeatherOnce(false)
        Log.d("WeatherActivity", "onResume: Updating cached states for visible locations.")
        displayedLocationOrder.forEach { locId -> 
            configuration?.findLocation(locId)?.let { location ->
                 if (location.preferences.showInApp) { 
                    updateState(location)
                }
            }
        }
    }

    private fun sortViews(weatherDataMap: HashMap<LocationIdentifier, WeatherData>) {
        configuration?.let { config ->
            val result = locationContainer.updateLocationOrder(weatherDataMap)

            val newOrder = result.sortedIdentifiers.filter { identifier ->
                config.findLocation(identifier)?.preferences?.showInApp == true
            }

            displayedLocationOrder.clear()
            displayedLocationOrder.addAll(newOrder)

            result.highlightedIdentifiers.forEach { identifier ->
                if (config.findLocation(identifier)?.preferences?.showInApp == true) { 
                    locationHighlightTriggers[identifier]?.value = true
                }
            }
        }
    }

    override fun onSharedPreferenceChanged(preferences: SharedPreferences, key: String?) {
        Log.d("WeatherActivity", "onSharedPreferenceChanged for key: $key")
        readConfiguration(preferences)
        updateWeatherOnce(true) 
    }


    private fun updateWeatherLocation(location: WeatherLocation, locationName: String, data: WeatherData) {
        locationCaptionStates[location.key]?.value = getCaption(locationName, data)
        locationWeatherStates[location.key]?.value = data
        configuration?.let {
            locationColorStates[location.key]?.value = ColorUtil.byAge(it.colorScheme, data.timestamp)
        }
        locationIsFavoriteStates[location.key]?.value = location.preferences.favorite
        locationShowForecastButtonStates[location.key]?.value = location.forecastUrl.toString().isNotBlank() && location.forecastUrl != Uri.EMPTY
    }

    private fun updateActivity(weatherDataResponse: FetchAllResponse, showToast: Boolean) {
        try {
            refreshLocationData(weatherDataResponse.weatherMap) 
            updateViewData(weatherDataResponse.weatherMap)      
            sortViews(weatherDataResponse.weatherMap)           
            updateWidgets(weatherDataResponse.weatherMap)       

            UserFeedback(applicationContext).showMessage(
                if (weatherDataResponse.valid) R.string.message_data_updated else R.string.message_data_update_failed, showToast
            )

            configuration?.let {
                val notificationManager = NotificationSystemManager(applicationContext, it)
                notificationManager.updateNotification(weatherDataResponse)
            }
        } catch (th: Throwable) {
            UserFeedback(applicationContext).showMessage(R.string.message_data_update_failed, true)
            Log.e(WeatherActivity::class.java.toString(), "Failed to update View", th)
        } finally {
            isWeatherUpdating.value = false
        }
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
        if (missingPermissions.isNotEmpty()) {
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
        isWeatherUpdating.value = true
        userLocationUpdater!!.updateLocation()
        val activityUpdateRequest = OneTimeWorkRequestBuilder<ActivityUpdateWorker>().addTag(ActivityUpdateWorker.WEATHER_DATA).build()
        val workManager = WorkManager.getInstance(applicationContext)
        workManager.enqueue(activityUpdateRequest)
        val workInfoByIdLiveData = workManager.getWorkInfoByIdLiveData(activityUpdateRequest.id)
        workInfoByIdLiveData.observe(this) { latestWorkInfo -> // Replaced Observer with lambda
            if (latestWorkInfo != null && latestWorkInfo.state.isFinished) {
                val weatherDataJson = latestWorkInfo.outputData.getString(ActivityUpdateWorker.WEATHER_DATA)
                val weatherData = Gson().fromJson(weatherDataJson, FetchAllResponse::class.java)
                updateActivity(weatherData, showToast)
            }
        }
    }

    private fun refreshLocationData(data: java.util.HashMap<LocationIdentifier, WeatherData>) {
        configuration?.locations?.forEach { location ->
            data[location.key]?.let { location.refresh(it) }
        }
    }

    private fun updateViewData(data: HashMap<LocationIdentifier, WeatherData>) {
        configuration?.locations?.forEach { location ->
            data[location.key]?.let {
                updateWeatherLocation(location, location.name, it)
            }
        }
    }

    private fun getCaption(locationName: String, data: WeatherData): String {
        var caption = "$locationName - ${data.localtime}"
        locationOrderStore?.let {
            if (it.readOrderCriteria() == OrderCriteria.location) {
                val userPosition = it.readPosition()
                val distance = userPosition.distanceTo(data.position)
                caption += " - ${formatter.formatDistance(distance.toFloat())}"
            }
        }
        return caption
    }

    private fun updateWidgets(data: HashMap<LocationIdentifier, WeatherData>) {
        val factory = ScreenPainterFactory(this, configuration!!)
        val screenPainters = factory.createScreenPainters()
        for (screenPainter in screenPainters) {
            screenPainter.updateWidgetData(data)
            screenPainter.showDataIsValid()
        }
    }
}
