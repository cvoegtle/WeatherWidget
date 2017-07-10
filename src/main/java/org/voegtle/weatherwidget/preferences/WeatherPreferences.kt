package org.voegtle.weatherwidget.preferences

import android.app.ActionBar
import android.os.Bundle
import org.voegtle.weatherwidget.base.ThemedActivity

class WeatherPreferences : ThemedActivity() {

  public override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    fragmentManager.beginTransaction()
        .replace(android.R.id.content, WeatherPreferenceFragment())
        .commit()

    actionBar?.setDisplayHomeAsUpEnabled(true)
  }

}
