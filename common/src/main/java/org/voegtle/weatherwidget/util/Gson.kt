package org.voegtle.weatherwidget.util

import com.google.gson.GsonBuilder

fun MyGson() = GsonBuilder()
    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ") // ISO 8601 Standard
    .create()
