package org.voegtle.weatherwidget.watch

import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService
import org.voegtle.weatherwidget.util.StatisticUpdateWorker
import org.voegtle.weatherwidget.widget.WidgetUpdateWorker

class WatchMessageListenerService : WearableListenerService() {

    override fun onMessageReceived(messageEvent: MessageEvent) {
        if (messageEvent.path == "/refresh-data") {
            val locationDataUpdateRequest = OneTimeWorkRequestBuilder<WidgetUpdateWorker>().build()
            WorkManager.getInstance(this).enqueue(locationDataUpdateRequest)
            val statisticsUpdateRequest = OneTimeWorkRequestBuilder<StatisticUpdateWorker>().build()
            WorkManager.getInstance(this).enqueue(statisticsUpdateRequest)
        }
    }
}
