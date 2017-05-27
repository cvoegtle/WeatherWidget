package org.voegtle.weatherwidget.widget

import android.content.Context
import android.util.Log
import org.voegtle.weatherwidget.data.WeatherData
import org.voegtle.weatherwidget.preferences.ApplicationSettings

class SmallWidgetUpdateTask(context: Context, configuration: ApplicationSettings,
                            private val screenPainter: SmallWidgetScreenPainter)
  : AbstractWidgetUpdateTask<String, Void, WeatherData>(context, configuration) {

  override fun onPreExecute() {
    screenPainter.showDataIsInvalid()
    screenPainter.updateAllWidgets()
  }


  override fun doInBackground(vararg weatherServerUrl: String): WeatherData? {
    return fetchWeatherData(weatherServerUrl[0])
  }

  override fun onPostExecute(data: WeatherData?) {
    try {
      screenPainter.updateData(data)
    } catch (th: Throwable) {
      Log.e(SmallWidgetUpdateTask::class.java.toString(), "Failed to update View", th)
    } finally {
      screenPainter.showDataIsValid()
    }

  }
}
