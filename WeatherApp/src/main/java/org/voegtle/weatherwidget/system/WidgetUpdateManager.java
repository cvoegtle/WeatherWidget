package org.voegtle.weatherwidget.system;

import android.content.Context;
import org.voegtle.weatherwidget.widget.WidgetRefreshService;

public class WidgetUpdateManager extends AbstractWidgetUpdateManager {


  public WidgetUpdateManager(Context context) {
    super(context, WidgetRefreshService.class);
  }
}
