package org.voegtle.weatherwidget

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.google.gson.Gson
import org.voegtle.weatherwidget.base.ThemedActivity
import org.voegtle.weatherwidget.base.UpdatingScrollView
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
import org.voegtle.weatherwidget.location.WeatherLocation
import org.voegtle.weatherwidget.notification.NotificationSystemManager
import org.voegtle.weatherwidget.preferences.ApplicationSettings
import org.voegtle.weatherwidget.preferences.ColorScheme
import org.voegtle.weatherwidget.preferences.NotificationSettings
import org.voegtle.weatherwidget.preferences.OrderCriteria
import org.voegtle.weatherwidget.preferences.OrderCriteriaDialogBuilder
import org.voegtle.weatherwidget.preferences.WeatherPreferences
import org.voegtle.weatherwidget.preferences.WeatherSettingsReader
import org.voegtle.weatherwidget.util.ActivityUpdateWorker
import org.voegtle.weatherwidget.util.ColorUtil
import org.voegtle.weatherwidget.util.DataFormatter
import org.voegtle.weatherwidget.util.FetchAllResponse
import org.voegtle.weatherwidget.util.StatisticsUpdater
import org.voegtle.weatherwidget.util.UserFeedback
import org.voegtle.weatherwidget.widget.ScreenPainterFactory

class WeatherActivity : ThemedActivity(), SharedPreferences.OnSharedPreferenceChangeListener {
    private var statisticsUpdater: StatisticsUpdater? = null
    private var configuration: ApplicationSettings? = null

    private lateinit var binding: ActivityWeatherBinding
    private var locationOrderStore: LocationOrderStore? = null
    private val formatter = DataFormatter()

    private var workInfo: LiveData<MutableList<WorkInfo>>? = null
    private var userLocationUpdater: UserLocationUpdater? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWeatherBinding.inflate(layoutInflater)

        setContentView(binding.root)
        binding.buttonGoogleDocs.setOnClickListener {
            val browserIntent = Intent(
                Intent.ACTION_VIEW, Uri.parse(
                    "https://docs.google.com/spreadsheets/d/1ahkm9SDTqjYcsLgKIH5yjmqlAh6dKxgfIrZA5Dt9L3o/edit?usp=sharing"
                )
            )
            startActivity(browserIntent)
        }

        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        readConfiguration(preferences)
        preferences.registerOnSharedPreferenceChangeListener(this)

        locationOrderStore = LocationOrderStore(this.applicationContext)
        statisticsUpdater = StatisticsUpdater(this)
        userLocationUpdater = UserLocationUpdater(this)

        patchPaddingForAndroid15()

        setupLocations()
        configureLocationSymbolColor()

        binding.scrollView.register(object : UpdatingScrollView.Updater {
            override fun update() {
                updateWeatherOnce(true)
            }
        })
    }

    override fun startActivity(intent: Intent?) {
        super.startActivity(intent)
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
        requestPermissions()
        enableNotificationsIfPermitted()
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
        readConfiguration(preferences)
        setupLocations()
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

        val observer = Observer<WorkInfo>() { latestWorkInfo ->
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
            refreshLocationData(weatherData.weatherMap)
            updateViewData(weatherData.weatherMap)
            sortViews(weatherData.weatherMap)
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

    private fun refreshLocationData(data: java.util.HashMap<LocationIdentifier, WeatherData>) {
        configuration!!.locations.forEach { location ->
            data[location.key]?.let {
                location.refresh(it)
            }
        }
    }

    private fun updateViewData(data: HashMap<LocationIdentifier, WeatherData>) {
        configuration!!.locations.forEach { location ->
            data[location.key]?.let {
                updateWeatherLocation(location, location.name, it)
            }
        }
    }

    private fun updateWeatherLocation(location: WeatherLocation, locationName: String, data: WeatherData) {
        val contentView: LocationView = findViewById(location.weatherViewId)

        val favorite = location.preferences.favorite
        highlightFavorite(contentView, favorite)


        val colorScheme = configuration!!.colorScheme
        val color = ColorUtil.byAge(colorScheme, data.timestamp)
        val caption = getCaption(locationName, data)

        updateView(contentView, caption, data, color)
    }

    private fun highlightFavorite(contentView: LocationView, favorite: Boolean) {
        contentView.setBackgroundColor(if (favorite) ColorUtil.favorite() else Color.TRANSPARENT)
    }


    private fun getCaption(locationName: String, data: WeatherData): String {
        var caption = "$locationName - ${data.localtime}"

        if (locationOrderStore!!.readOrderCriteria() == OrderCriteria.location) {
            val userPosition = locationOrderStore!!.readPosition()
            val distance = userPosition.distanceTo(data.position)
            caption += " - ${formatter.formatDistance(distance.toFloat())}"
        }

        return caption
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

    private fun sortViews(data: HashMap<LocationIdentifier, WeatherData>) {
        val container = locationContainer()
        val locationContainer = LocationContainer(applicationContext, container, configuration!!)
        locationContainer.updateLocationOrder(data)
    }


    private fun patchPaddingForAndroid15() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            locationContainer().setPadding(0, 310, 0, 0)
        }
    }


    private fun locationContainer() = binding.locationContainer


}
