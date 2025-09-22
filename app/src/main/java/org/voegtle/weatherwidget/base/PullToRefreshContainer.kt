package org.voegtle.weatherwidget.base // Oder ein anderes passendes Package

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/**
 * Ein Composable Container, der "Pull-to-Refresh"-Funktionalität bereitstellt.
 * Er ersetzt den alten UpdatingScrollView.
 *
 * @param isUpdating Gibt an, ob der Ladeindikator angezeigt werden soll (wird von außen gesteuert).
 * @param onUpdateRequested Lambda, das aufgerufen wird, wenn eine Aktualisierung durch die Geste ausgelöst wird.
 * @param modifier Modifier für diesen Container.
 * @param content Der Inhalt, der scrollbar sein soll und die Pull-to-Refresh-Geste unterstützt.
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PullToRefreshContainer(
    isUpdating: Boolean,
    onUpdateRequested: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    var lastUpdateRequestedTimestamp by remember { mutableStateOf(0L) }

    val pullRefreshState = rememberPullRefreshState(
        refreshing = isUpdating, 
        onRefresh = {
            val currentTime = System.currentTimeMillis()
            if ((currentTime - lastUpdateRequestedTimestamp) > 5000L) { // 5000ms = 5 Sekunden
                lastUpdateRequestedTimestamp = currentTime
                onUpdateRequested() 
            }
        }
    )

    Box(modifier = modifier.pullRefresh(pullRefreshState)) {
        content() 

        PullRefreshIndicator(
            refreshing = isUpdating,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}
