package org.voegtle.wetterwolkewatch.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.pager.HorizontalPager
import androidx.wear.compose.foundation.pager.PagerState
import androidx.wear.compose.foundation.pager.VerticalPager
import androidx.wear.compose.material.HorizontalPageIndicator
import androidx.wear.compose.material.PageIndicatorState
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.Text
import org.voegtle.weatherwidget.data.LocationDataSet
import org.voegtle.weatherwidget.data.Statistics
import org.voegtle.weatherwidget.data.StatisticsSet
import org.voegtle.weatherwidget.data.WeatherData
import org.voegtle.weatherwidget.util.DataFormatter
import org.voegtle.wetterwolkewatch.R
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun WeatherListScreen(locationDataSetList: List<LocationDataSet>, resetPager: Int) {
    val pageCount = locationDataSetList.size
    val pagerState = remember(resetPager) {
        PagerState(
            currentPage = Int.MAX_VALUE / 2,
            pageCount = { Int.MAX_VALUE }
        )
    }

    Scaffold(
        positionIndicator = {
            HorizontalPageIndicator(
                pageIndicatorState = remember(pagerState.currentPage, pagerState.currentPageOffsetFraction) {
                    object : PageIndicatorState {
                        override val pageCount: Int
                            get() = pageCount
                        override val selectedPage: Int
                            get() = (pagerState.currentPage - Int.MAX_VALUE / 2).mod(pageCount)
                        override val pageOffset: Float
                            get() = pagerState.currentPageOffsetFraction
                    }
                }
            )
        }
    ) {
        HorizontalPager(state = pagerState) { page ->
            val actualPage = (page - Int.MAX_VALUE / 2).mod(pageCount)
            WeatherScreen(locationDataSet = locationDataSetList[actualPage], page = actualPage)
        }
    }
}

private const val CURRENT_TODAY_YESTERDAY_WEEK = 5
private const val CURRENT = 0
private const val TODAY = 1
private const val YESTERDAY = 2
private const val WEEK = 3
private const val MONTH = 4


@Composable
fun WeatherScreen(locationDataSet: LocationDataSet, page: Int) {
    val statistics = locationDataSet.statistics

    if (statistics != null) {
        val verticalPagerState = remember {
            PagerState(
                currentPage = CURRENT,
                pageCount = { CURRENT_TODAY_YESTERDAY_WEEK }
            )
        }
        VerticalPager(state = verticalPagerState) { verticalPage ->
            when (verticalPage) {
                CURRENT -> WeatherMainScreen(locationDataSet = locationDataSet, page = page)
                TODAY -> WeatherStatisticsScreen(
                    stringResource(R.string.today),
                    statistics.receiveTime,
                    statistics[Statistics.TimeRange.today]!!,
                    page = page
                )

                YESTERDAY -> WeatherStatisticsScreen(
                    stringResource(R.string.yesterday),
                    statistics.receiveTime,
                    statistics[Statistics.TimeRange.yesterday]!!,
                    page = page
                )

                WEEK -> WeatherStatisticsScreen(
                    stringResource(R.string.week),
                    statistics.receiveTime,
                    statistics[Statistics.TimeRange.last7days]!!,
                    page = page
                )

                MONTH -> WeatherStatisticsScreen(
                    stringResource(R.string.month),
                    statistics.receiveTime,
                    statistics[Statistics.TimeRange.last30days]!!,
                    page = page
                )
            }
        }
    } else {
        WeatherMainScreen(locationDataSet = locationDataSet, page = page)
    }
}

@Composable
fun WeatherMainScreen(locationDataSet: LocationDataSet, page: Int) {
    val weatherData = locationDataSet.weatherData
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = weatherData.localtime,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 8.dp)
        )

        Box(
            modifier = Modifier
                .background(backgroundColor(page), CircleShape)
                .padding(18.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val formatter = DataFormatter()
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val isFavorite = locationDataSet.weatherLocation.preferences.favorite
                    if (isFavorite) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Favorite",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    Text(
                        text = captionLocationShortcut(weatherData),
                        style = MaterialTheme.typography.displayLarge,
                    )
                }
                Text(
                    text = textTemperatureHumidityCombined(formatter, weatherData),
                    style = MaterialTheme.typography.bodyLarge
                )
                weatherData.rainToday?.let {
                    Text(
                        text = textRain(formatter, weatherData.rainToday, weatherData),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                weatherData.barometer?.let {
                    Text(
                        text = formatter.formatBarometer(it),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}

@Composable
fun WeatherStatisticsScreen(caption: String, timestamp: Date?, stats: StatisticsSet, page: Int) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = formatTime(timestamp),
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 8.dp)
        )

        Box(
            modifier = Modifier
                .background(backgroundColor(page), CircleShape)
                .padding(18.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val formatter = DataFormatter()
                Text(
                    text = caption,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                stats.minTemperature?.let {
                    Text(
                        text = "T: ${formatter.formatTemperature(it)} - ${formatter.formatTemperature(stats.maxTemperature)}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                stats.rain?.let {
                    Text(
                        text = stringResource(R.string.rain_label) + " " + formatter.formatRain(it),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                stats.solarRadiationMax?.let {
                    Text(
                        text = stringResource(R.string.solar_max) + " " + formatter.formatSolarradiation(it),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                stats.kwh?.let {
                    Text(
                        text = stringResource(R.string.total_solar) + " " + formatter.formatKwh(it),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}

@Composable
private fun formatTime(timestamp: Date?): String =
    if (timestamp == null)
        "-:-"
    else
        SimpleDateFormat("HH:mm", LocalLocale.current.platformLocale).format(timestamp)


@Composable
private fun backgroundColor(page: Int): Color =
    if (page == CURRENT) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainer

@Composable
private fun textRain(
    formatter: DataFormatter,
    rainToday: Float?,
    weatherData: WeatherData
): String =
    stringResource(R.string.rain_label) + formatter.formatRain(rainToday) + if (weatherData.rain != null) " / ${formatter.formatRain(weatherData.rain)}" else ""

@Composable
private fun textTemperatureHumidityCombined(formatter: DataFormatter, weatherData: WeatherData): String =
    "${formatter.formatTemperature(weatherData.temperature)} / ${formatter.formatHumidity(weatherData.humidity)}"

@Composable
private fun captionLocationShortcut(weatherData: WeatherData): String = weatherData.location_short
