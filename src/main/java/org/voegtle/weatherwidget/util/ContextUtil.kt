package org.voegtle.weatherwidget.util

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import org.voegtle.weatherwidget.widget.WidgetUpdateTask

object ContextUtil {

  fun getBuildNumber(context: Context): Int {
    var buildNumber: Int = -1
    try {
      val packageManager = context.packageManager
      val packageName = context.packageName
      buildNumber = packageManager.getPackageInfo(packageName, 0).versionCode
    } catch (ex: PackageManager.NameNotFoundException) {
      Log.e(ContextUtil::class.java.toString(), "Failed to retrieve build number", ex)
    }

    return buildNumber
  }

  fun getVersion(context: Context): String {
    var version: String = "unknown"
    try {
      val packageManager = context.packageManager
      val packageName = context.packageName
      version = packageManager.getPackageInfo(packageName, 0).versionName
    } catch (ex: PackageManager.NameNotFoundException) {
      Log.e(ContextUtil::class.java.toString(), "Failed to retrieve app version", ex)
    }

    return version
  }

}
