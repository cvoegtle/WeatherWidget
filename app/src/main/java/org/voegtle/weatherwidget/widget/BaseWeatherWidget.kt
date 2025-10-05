package org.voegtle.weatherwidget.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.ColorFilter
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.components.Scaffold
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.background
import androidx.glance.color.ColorProvider
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import androidx.preference.PreferenceManager
import com.google.gson.Gson
import org.voegtle.weatherwidget.R
import org.voegtle.weatherwidget.WeatherActivity
import org.voegtle.weatherwidget.location.LocationDataSet
import org.voegtle.weatherwidget.preferences.WeatherPreferences
import org.voegtle.weatherwidget.preferences.WeatherPreferencesReader
import org.voegtle.weatherwidget.preferences.WidgetPreferences
import org.voegtle.weatherwidget.util.DateUtil

private const val WIDGET_DATA_KEY = "weather_lines"

abstract class BaseWeatherWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            Content(context)
        }
    }

    @Composable
    private fun Content(context: Context) {
        val widgetPreferences = readPreferences(context)
        val locationDataSets = loadDataSetsFromPreferences()
        val fontSize = determineFontSize(locationDataSets, widgetPreferences)

        GlanceTheme {
            Scaffold(
                backgroundColor = GlanceTheme.colors.surface,
                modifier = GlanceModifier.clickable(onClick = actionStartActivity<WeatherActivity>()).padding(top = 4.dp)
            ) {
                if (locationDataSets.isNotEmpty()) {
                    Column(modifier = GlanceModifier.fillMaxSize()) {
                        LazyColumn(GlanceModifier.defaultWeight()) {
                            items(items = locationDataSets) { dataSet ->
                                Column(modifier = GlanceModifier.padding(vertical = determineGap())) {
                                    WeatherRow(dataSet, fontSize, context)
                                }
                            }
                        }
                        LastUpdateTime()
                    }
                } else {
                    PlaceHolder()
                }
            }
        }
    }

    @Composable
    private fun loadDataSetsFromPreferences(): List<LocationDataSet> {
        val prefs = currentState<Preferences>()
        val jsonLocationDataSets = prefs[stringPreferencesKey(WIDGET_DATA_KEY)]
        return if (jsonLocationDataSets == null)
            emptyList()
        else
            Gson().fromJson(jsonLocationDataSets, Array<LocationDataSet>::class.java).toList()
    }

    @Composable
    private fun WeatherRow(locationDataSet: LocationDataSet, fontSize: TextUnit, context: Context) {
        val widgetPreferences = readPreferences(context)
        Row(
            modifier = GlanceModifier
                .fillMaxWidth()
                .clickable(onClick = actionStartActivity<WeatherActivity>())
                .background(determineRowBackground(locationDataSet))
                .padding(horizontal = 2.dp, vertical = determinePadding())
                .cornerRadius(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                provider = ImageProvider(R.drawable.ic_filled_circle),
                contentDescription = "Regenindikator",
                modifier = GlanceModifier
                    .size(13.dp)
                    .padding(top = 1.dp),
                colorFilter = determineIconColor(locationDataSet)
            )
            Text(
                modifier = GlanceModifier.padding(start = 2.dp),
                text = assembleWeatherText(locationDataSet, widgetPreferences),
                style = TextStyle(color = determineTextColor(locationDataSet), fontSize = fontSize, fontWeight = determineFontWeight())
            )
        }
    }

    @Composable
    private fun LastUpdateTime() {
        Row(
            modifier = GlanceModifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(GlanceModifier.defaultWeight())
            Text(
                text = DateUtil.currentTime,
                style = TextStyle(
                    color = GlanceTheme.colors.onSurface,
                    fontSize = 11.sp
                ),
                modifier = GlanceModifier.clickable(
                    onClick = if (isSettingsButtonVisible())
                        actionStartActivity<WeatherPreferences>()
                    else
                        actionStartActivity<WeatherActivity>()
                )
            )
            if (isSettingsButtonVisible()) {
                Spacer(GlanceModifier.size(8.dp))
                Image(
                    provider = ImageProvider(R.drawable.ic_settings),
                    contentDescription = "Einstellungen",
                    modifier = GlanceModifier.size(16.dp).clickable(onClick = actionStartActivity<WeatherPreferences>()),
                    colorFilter = determineSettingsColor()
                )
            }
        }
    }

    @Composable
    private fun PlaceHolder() {
        Box(
            modifier = GlanceModifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Laden...",
                modifier = GlanceModifier.padding(8.dp),
                style = TextStyle(color = GlanceTheme.colors.onSurface)
            )
        }
    }

    @Composable
    abstract fun assembleWeatherText(locationDataSet: LocationDataSet, widgetPreferences: WidgetPreferences): String

    @Composable
    abstract fun determineFontSize(locationDataSets: List<LocationDataSet>, widgetPreferences: WidgetPreferences): TextUnit

    abstract fun determineFontWeight(): FontWeight

    abstract fun determineGap(): Dp

    abstract fun determinePadding(): Dp

    abstract fun isSettingsButtonVisible(): Boolean

    @Composable
    private fun determineRowBackground(locationDataSet: LocationDataSet): ColorProvider =
        when {
            DateUtil.isOutdated(locationDataSet.weatherData.timestamp) -> GlanceTheme.colors.error
            locationDataSet.weatherLocation.preferences.favorite -> GlanceTheme.colors.primary
            else -> GlanceTheme.colors.surfaceVariant
        }

    @Composable
    private fun determineIconColor(locationDataSet: LocationDataSet): ColorFilter =
        when {
            DateUtil.isOutdated(locationDataSet.weatherData.timestamp) -> ColorFilter.tint(GlanceTheme.colors.onError)
            locationDataSet.weatherData.isRaining -> ColorFilter.tint(ColorProvider(Color.Blue, Color.Cyan))
            locationDataSet.weatherLocation.preferences.favorite -> ColorFilter.tint(GlanceTheme.colors.onPrimary)
            else -> ColorFilter.tint(GlanceTheme.colors.onSurface)
        }
    @Composable
    private fun determineSettingsColor(): ColorFilter = ColorFilter.tint(GlanceTheme.colors.onSurface)

    @Composable
    private fun determineTextColor(locationDataSet: LocationDataSet): ColorProvider =
        when {
            DateUtil.isOutdated(locationDataSet.weatherData.timestamp) -> GlanceTheme.colors.onError
            locationDataSet.weatherLocation.preferences.favorite -> GlanceTheme.colors.onPrimary
            else -> GlanceTheme.colors.onSurface
        }

    protected fun readPreferences(context: Context): WidgetPreferences {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val (_, widgetPreferences, _) = WeatherPreferencesReader(context.resources).read(preferences)
        return widgetPreferences
    }

}

// This function is called by the worker to update the state
suspend fun updateWeatherWidgetState(context: Context, locationDataSets: List<LocationDataSet>) {
    updateWeatherWidgetState(context, locationDataSets, TemperatureWidget::class.java)
    updateWeatherWidgetState(context, locationDataSets, WeatherDetailsWidget::class.java)
}

suspend fun <T : GlanceAppWidget> updateWeatherWidgetState(context: Context, locationDataSets: List<LocationDataSet>, provider: Class<T>) {
    val widgetRelevantLocationDataSets = locationDataSets.filter { it.weatherLocation.preferences.showInWidget }.toList()
    val glanceManager = GlanceAppWidgetManager(context)
    val glanceIds = glanceManager.getGlanceIds(provider)
    glanceIds.forEach { glanceId ->
        updateAppWidgetState(context, glanceId) { prefs ->
            val jsonLocationDataSets = Gson().toJson(widgetRelevantLocationDataSets)
            val key = stringPreferencesKey(WIDGET_DATA_KEY)
            prefs[key] = jsonLocationDataSets
        }
        provider.getDeclaredConstructor().newInstance().update(context, glanceId)
    }
}
