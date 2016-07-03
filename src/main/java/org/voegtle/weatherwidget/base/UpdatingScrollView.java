package org.voegtle.weatherwidget.base;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;
import org.voegtle.weatherwidget.util.DateUtil;

import java.util.Date;

public class UpdatingScrollView extends ScrollView {
  public interface Updater {
    void update();
  }
  private Date lastUpdate = new Date();
  private Updater updater;

  public UpdatingScrollView(Context context) {
    super(context);
  }

  public UpdatingScrollView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public UpdatingScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  public void register(Updater updater) {
    this.updater = updater;
  }


  @Override
  protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
    super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
    if (updater != null) {
      if (scrollY == 0 && clampedY && DateUtil.getAge(lastUpdate) > 5) {
        lastUpdate = new Date();
        updater.update();
      }
    }
  }
}
