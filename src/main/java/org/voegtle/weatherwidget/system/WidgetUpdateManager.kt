package org.voegtle.weatherwidget.system

import android.content.Context
import org.voegtle.weatherwidget.widget.WidgetRefreshService

class WidgetUpdateManager(context: Context) : AbstractWidgetUpdateManager(context, WidgetRefreshService::class.java)
