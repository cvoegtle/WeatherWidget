package org.voegtle.wetterwolkewatch.presentation

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.Text
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import kotlinx.coroutines.launch
import org.voegtle.weatherwidget.data.LocationDataSet
import org.voegtle.wetterwolkewatch.ACTION_DATA_UPDATED
import org.voegtle.wetterwolkewatch.R
import org.voegtle.wetterwolkewatch.io.AppMessenger
import org.voegtle.wetterwolkewatch.io.WatchDataStore
import org.voegtle.wetterwolkewatch.ui.WeatherScreen


@OptIn(ExperimentalPagerApi::class)
class WetterWatchActivity : ComponentActivity() {

    private var locationDataSetList by mutableStateOf<List<LocationDataSet>>(emptyList())

    private val dataUpdateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == ACTION_DATA_UPDATED) {
                locationDataSetList = WatchDataStore(this@WetterWatchActivity).readDataFromFile()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestUpdatedData()
        locationDataSetList = WatchDataStore(this).readDataFromFile()

        setContent {
            MaterialTheme {
                if (locationDataSetList.isNotEmpty()) {
                    HorizontalPager(count = locationDataSetList.size) { page ->
                        WeatherScreen(weatherData = locationDataSetList[page].weatherData)
                    }
                } else {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(stringResource(R.string.waiting_for_data))
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        requestUpdatedData()
        locationDataSetList = WatchDataStore(this).readDataFromFile()
        val filter = IntentFilter(ACTION_DATA_UPDATED)
        LocalBroadcastManager.getInstance(this).registerReceiver(dataUpdateReceiver, filter)
    }

    private fun requestUpdatedData() {
        lifecycleScope.launch {
            AppMessenger(this@WetterWatchActivity).requestDataUpdate()
        }
    }

    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(dataUpdateReceiver)
    }

}
