package org.voegtle.weatherwidget.util

import android.content.Context

object ContextUtil {

  fun getBuildNumber(context: Context): Int {
    val packageManager = context.packageManager
    val packageName = context.packageName
    return packageManager.getPackageInfo(packageName, 0).versionCode
  }

  fun getVersion(context: Context): String {
    val packageManager = context.packageManager
    val packageName = context.packageName
    return packageManager.getPackageInfo(packageName, 0).versionName
  }

}
