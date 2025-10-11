package org.voegtle.weatherwidget

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import org.voegtle.weatherwidget.location.LocationIdentifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherTopAppBar(
    onReloadClicked: () -> Unit,
    onDiagramClicked: (LocationIdentifier?) -> Unit,
    onReorderClicked: () -> Unit,
    onPreferencesClicked: () -> Unit,
) {
    TopAppBar(
        title = { Text(stringResource(id = R.string.app_name)) },
        actions = {
            IconButton(onClick = { onReloadClicked() }) {
                Icon(Icons.Default.Refresh, contentDescription = stringResource(id = R.string.action_reload))
            }
            IconButton(onClick = { onDiagramClicked(null) }) {
                Icon(Icons.Default.BarChart, contentDescription = stringResource(id = R.string.action_diagrams))
            }
            IconButton(onClick = { onReorderClicked() }) {
                Icon(Icons.AutoMirrored.Filled.Sort, contentDescription = stringResource(id = R.string.action_sort))
            }
            IconButton(onClick = onPreferencesClicked) {
                Icon(Icons.Default.Settings, contentDescription = stringResource(id = R.string.action_preferences))
            }
        }
    )
}
