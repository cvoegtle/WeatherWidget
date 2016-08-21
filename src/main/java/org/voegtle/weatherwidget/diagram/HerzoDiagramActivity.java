package org.voegtle.weatherwidget.diagram;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import org.voegtle.weatherwidget.R;
import org.voegtle.weatherwidget.util.StringUtil;

public class HerzoDiagramActivity extends DiagramActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    addDiagram(DiagramEnum.herzo_regen);
    addDiagram(DiagramEnum.herzo_wind);
    addDiagram(DiagramEnum.herzo_lastyear);
    Intent intent = getIntent();
    if (intent != null) {
      String title = intent.getStringExtra(HerzoDiagramActivity.class.getName());
      if (StringUtil.isNotEmpty(title)) {
        setTitle(title);
      }
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.herzo_diagram_activity_menu, menu);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  protected boolean onCustomItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_herzo_rain:
        viewPager.setCurrentItem(0, true);
        return true;
      case R.id.action_herzo_wind:
        viewPager.setCurrentItem(1, true);
        return true;
      case R.id.action_herzo_last_year:
        viewPager.setCurrentItem(2, true);
        return true;
    }
    return false;
  }
}
