package org.voegtle.weatherwidget

import android.app.Application
import com.google.android.material.color.DynamicColors

class WeatherWidgetApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // This line enables dynamic colors (Material You) for all activities
        DynamicColors.applyToActivitiesIfAvailable(this)
    }
}
