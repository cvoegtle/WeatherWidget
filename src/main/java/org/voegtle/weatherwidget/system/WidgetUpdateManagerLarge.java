package org.voegtle.weatherwidget.system;

import android.content.Context;
import org.voegtle.weatherwidget.widget.WidgetRefreshServiceLarge;

public class WidgetUpdateManagerLarge extends AbstractWidgetUpdateManager {
  public WidgetUpdateManagerLarge(Context context) {
    super(context, WidgetRefreshServiceLarge.class);
  }
}
