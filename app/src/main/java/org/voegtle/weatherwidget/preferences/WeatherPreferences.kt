package org.voegtle.weatherwidget.preferences

import android.os.Bundle
import android.preference.PreferenceManager // Import für Theme-Logik
import android.view.MenuItem // Import für onOptionsItemSelected
import androidx.appcompat.app.AppCompatActivity // Geändert
import com.google.android.material.appbar.MaterialToolbar
// import org.voegtle.weatherwidget.base.ThemedActivity // Entfernt
import org.voegtle.weatherwidget.R // Import für R.layout.activity_weather_preferences etc.

class WeatherPreferences : AppCompatActivity() { // Basisklasse geändert

    private fun configureTheme() {
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        val weatherSettingsReader = WeatherSettingsReader(this.applicationContext)
        val configuration = weatherSettingsReader.read(preferences)
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        configureTheme() // Theme setzen vor super.onCreate und setContentView
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather_preferences) // Neues Layout verwenden

        setupToolbar()

        // WeatherPreferenceFragment in den Container laden
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, WeatherPreferenceFragment())
                .commit()
        }
    }

    private fun setupToolbar() {
        val toolbar: MaterialToolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.title = getString(R.string.action_preferences)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> { // Behandelt Klick auf den Zurück-Pfeil in der Toolbar
                onBackPressedDispatcher.onBackPressed() // Moderne Art der Zurück-Navigation
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
