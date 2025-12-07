package org.voegtle.wetterwolkewatch

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.Text
import org.voegtle.weatherwidget.data.WeatherData
import org.voegtle.weatherwidget.location.LocationIdentifier
import org.voegtle.weatherwidget.location.Position
import java.util.Date

@Composable
fun WeatherScreen(weatherData: WeatherData) {
    Column(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "${"%.1f".format(weatherData.temperature)}°C",
            style = MaterialTheme.typography.displayMedium
        )
        Text(
            text = "Luftfeuchtigkeit: ${"%.1f".format(weatherData.humidity)}%",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Preview(showBackground = true)
@Composable
fun WeatherScreenPreview() {
    // Dummy-Daten für die Vorschau
    val previewData = WeatherData(
        location = LocationIdentifier.Paderborn,
        timestamp = Date(),
        temperature = 23.5f,
        humidity = 65.2f,
        localtime = "14:30",
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
    MaterialTheme { // Eine Theme-Wrapper ist für die Vorschau erforderlich
        WeatherScreen(weatherData = previewData)
    }
}