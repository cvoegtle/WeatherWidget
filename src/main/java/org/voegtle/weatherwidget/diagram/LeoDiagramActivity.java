package org.voegtle.weatherwidget.diagram;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import org.voegtle.weatherwidget.R;
import org.voegtle.weatherwidget.util.StringUtil;

public class LeoDiagramActivity extends DiagramActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    addDiagram(DiagramEnum.leo_regen);
    addDiagram(DiagramEnum.leo_wind);
    addDiagram(DiagramEnum.leo_solar);
    addDiagram(DiagramEnum.leo_solar_average);
    addDiagram(DiagramEnum.leo_solar_production);
    addDiagram(DiagramEnum.leo_paderborn);
    addDiagram(DiagramEnum.leo_lastyear);
    Intent intent = getIntent();
    if (intent != null) {
      String title = intent.getStringExtra(LeoDiagramActivity.class.getName());
      if (StringUtil.isNotEmpty(title)) {
        setTitle(title);
      }
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.leo_diagram_activity_menu, menu);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  protected boolean onCustomItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_leo_rain:
        viewPager.setCurrentItem(0, true);
        return true;
      case R.id.action_leo_wind:
        viewPager.setCurrentItem(1, true);
        return true;
      case R.id.action_leo_solar:
        viewPager.setCurrentItem(2, true);
        return true;
      case R.id.action_solar_daily:
        viewPager.setCurrentItem(3, true);
        break;
      case R.id.action_solar_monthly:
        viewPager.setCurrentItem(4, true);
        break;
      case R.id.action_leo_paderborn:
        viewPager.setCurrentItem(5, true);
        return true;
      case R.id.action_leo_last_year:
        viewPager.setCurrentItem(6, true);
        return true;
    }
    return false;
  }
}
