package org.voegtle.weatherwidget.preferences

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import org.voegtle.weatherwidget.R

class WeatherPreferences : AppCompatActivity() {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather_preferences)

        setupToolbar()

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
            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
