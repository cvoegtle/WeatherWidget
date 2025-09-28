package org.voegtle.weatherwidget.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp // Added import for sp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.ImageProvider
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.components.Scaffold
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.updateAppWidgetState
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
import com.google.gson.Gson
import org.voegtle.weatherwidget.R
import org.voegtle.weatherwidget.location.LocationDataSet
import org.voegtle.weatherwidget.util.DataFormatter
import androidx.glance.Image
import androidx.glance.ColorFilter
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.background
import androidx.glance.color.ColorProvider
import androidx.glance.unit.ColorProvider
import org.voegtle.weatherwidget.WeatherActivity
import org.voegtle.weatherwidget.util.DateUtil

private const val WIDGET_DATA_KEY = "weather_lines"

abstract class BaseWeatherGlanceWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            Content()
        }
    }

    @Composable
    private fun Content() {
        val locationDataSets = loadDataSetsFromPreferences()

        GlanceTheme {
            Scaffold(
                backgroundColor = GlanceTheme.colors.surface,
                modifier = GlanceModifier.clickable(onClick = actionStartActivity<WeatherActivity>())
            ) {
                if (locationDataSets.isNotEmpty()) {
                    Column(modifier = GlanceModifier.fillMaxSize()) { 
                        LazyColumn(GlanceModifier.defaultWeight()) {
                            items(items = locationDataSets) { dataSet ->
                                WeatherRow(dataSet)
                            }
                        }
                        Row(
                            modifier = GlanceModifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Spacer(GlanceModifier.defaultWeight()) 
                            Text(
                                text = DateUtil.currentTime,
                                style = TextStyle(
                                    color = GlanceTheme.colors.onSurface,
                                    fontSize = 12.sp // Font size changed
                                )
                            )
                        }
                    }
                } else {
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
    private fun WeatherRow(locationDataSet: LocationDataSet) {
        val formatter = DataFormatter()
        Row(
            modifier = GlanceModifier
                .fillMaxWidth()
                .clickable(onClick = actionStartActivity<WeatherActivity>()) // Added clickable modifier here
                .padding(horizontal = 2.dp, vertical = 2.dp)
                .background(determineRowBackground(locationDataSet)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                provider = ImageProvider(R.drawable.ic_filled_circle),
                contentDescription = "Regenindikator",
                modifier = GlanceModifier
                    .size(13.dp)
                    .padding(top = 2.dp),
                colorFilter = determineIconColor(locationDataSet)
            )
            Text(
                modifier = GlanceModifier.padding(start = 4.dp),
                text = assembleWeatherText(locationDataSet, formatter),
                style = TextStyle(fontWeight = FontWeight.Bold, color = determineTextColor(locationDataSet))
            )
        }
    }

    protected abstract fun assembleWeatherText(locationDataSet: LocationDataSet, formatter: DataFormatter): String

    @Composable
    private fun determineRowBackground(locationDataSet: LocationDataSet): ColorProvider =
        when {
            DateUtil.isOutdated(locationDataSet.weatherData.timestamp) -> GlanceTheme.colors.error
            locationDataSet.weatherLocation.preferences.favorite -> GlanceTheme.colors.primary
            else -> GlanceTheme.colors.surface
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
    private fun determineTextColor(locationDataSet: LocationDataSet): ColorProvider =
        when {
            DateUtil.isOutdated(locationDataSet.weatherData.timestamp) -> GlanceTheme.colors.onError
            locationDataSet.weatherLocation.preferences.favorite -> GlanceTheme.colors.onPrimary
            else -> GlanceTheme.colors.onSurface
        }

}

// This function is called by the worker to update the state
suspend fun updateWeatherWidgetState(context: Context, locationDataSets: List<LocationDataSet>) {
    updateWeatherWidgetState(context, locationDataSets, SmallGlanceWidget::class.java)
    updateWeatherWidgetState(context, locationDataSets, LargeGlanceWidget::class.java)
}

suspend fun <T : GlanceAppWidget>updateWeatherWidgetState(context: Context, locationDataSets: List<LocationDataSet>, provider: Class<T>) {
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
