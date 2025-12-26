package org.voegtle.wetterwolkewatch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.wear.compose.material.MaterialTheme
import org.voegtle.weatherwidget.data.WeatherData
import org.voegtle.weatherwidget.location.LocationIdentifier
import org.voegtle.weatherwidget.location.Position
import org.voegtle.wetterwolkewatch.ui.WeatherScreen
import java.util.Date

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // TODO: Ersetze diese Dummy-Daten durch echte Daten von der Smartphone-App
            // (z.B. über die Wearable Data Layer API)
            val dummyData = WeatherData(
                location = LocationIdentifier.Paderborn,
                timestamp = Date(),
                temperature = 19.8f,
                humidity = 72.0f,
                localtime = "10:00",
                position = Position(0.0f, 0.0f),
                insideTemperature = null,
                insideHumidity = null,
                barometer = null,
                solarradiation = null,
                UV = null,
                rain = null,
                rainToday = null,
                isRaining = false,
                watt = null,
                powerProduction = null,
                powerFeed = null,
                wind = null,
                windgust = null,
                location_name = "Paderborn",
                location_short = "PB",
                forecast = null
            )

            // Hier wird das Theme und der Screen geladen
            MaterialTheme {
                WeatherScreen(weatherData = dummyData)
            }
        }
    }
}