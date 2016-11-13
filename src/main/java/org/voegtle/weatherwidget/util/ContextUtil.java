package org.voegtle.weatherwidget.util;

import android.content.Context;
import android.content.pm.PackageManager;

public class ContextUtil {

  public static Integer getBuildNumber(Context context) {
    Integer buildNumber = null;
    try {
      PackageManager packageManager = context.getPackageManager();
      String packageName = context.getPackageName();
      buildNumber = packageManager.getPackageInfo(packageName, 0).versionCode;
    } catch (PackageManager.NameNotFoundException ex) {
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
    }
    return version;
  }

}
