package org.voegtle.weatherwidget.util

import android.content.Context
import android.content.res.Resources
import android.preference.PreferenceManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import org.voegtle.weatherwidget.preferences.ApplicationSettings
import org.voegtle.weatherwidget.preferences.WeatherSettingsReader

abstract class UpdateWorker (appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {
    private var res: Resources = applicationContext.resources
    protected val weatherDataFetcher = WeatherDataFetcher(ContextUtil.getBuildNumber(appContext))
    protected val configuration: ApplicationSettings

    init {
        val preferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)

        val weatherSettingsReader = WeatherSettingsReader(res)
        configuration = weatherSettingsReader.read(preferences)
    }
}