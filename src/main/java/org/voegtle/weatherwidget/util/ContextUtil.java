package org.voegtle.weatherwidget.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;
import org.voegtle.weatherwidget.widget.WidgetUpdateTask;

public class ContextUtil {

  public static Integer getBuildNumber(Context context) {
    Integer buildNumber = null;
    try {
      PackageManager packageManager = context.getPackageManager();
      String packageName = context.getPackageName();
      buildNumber = packageManager.getPackageInfo(packageName, 0).versionCode;
    } catch (PackageManager.NameNotFoundException ex) {
      Log.e(ContextUtil.class.toString(), "Failed to retrieve build number", ex);
    }
    return buildNumber;
  }

  public static String getVersion(Context context) {
    String version = null;
    try {
      PackageManager packageManager = context.getPackageManager();
      String packageName = context.getPackageName();
      version = packageManager.getPackageInfo(packageName, 0).versionName;
    } catch (PackageManager.NameNotFoundException ex) {
      Log.e(ContextUtil.class.toString(), "Failed to retrieve app version", ex);
    }
    return version;
  }

}
