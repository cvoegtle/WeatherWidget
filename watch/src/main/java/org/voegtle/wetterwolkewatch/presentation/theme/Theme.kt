package org.voegtle.wetterwolkewatch.presentation.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.dynamicColorScheme

@Composable
fun WeatherWidgetTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = dynamicColorScheme(LocalContext.current)
    MaterialTheme(
        colorScheme = colorScheme!!,
        content = content
    )
}
