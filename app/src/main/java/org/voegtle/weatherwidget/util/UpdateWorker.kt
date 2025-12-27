package org.voegtle.weatherwidget.util

import android.content.Context
import android.preference.PreferenceManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import org.voegtle.weatherwidget.preferences.ApplicationPreferences
import org.voegtle.weatherwidget.preferences.WeatherPreferencesReader

abstract class UpdateWorker (appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {
    protected val weatherDataFetcher = WeatherDataFetcher(ContextUtil.getBuildNumber(appContext))
    protected val configuration: ApplicationPreferences

    init {
        val weatherPreferencesReader = WeatherPreferencesReader(applicationContext)
        configuration = weatherPreferencesReader.read()
    }
}