package org.voegtle.weatherwidget.diagram;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import org.voegtle.weatherwidget.R;

public class BaliDiagramActivity extends DiagramActivity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    addDiagram((DiagramEnum.bali_7days));
    addDiagram(DiagramEnum.bali_wind);
    addDiagram(DiagramEnum.bali_humidity);
    addDiagram(DiagramEnum.bali_30days);
    addDiagram(DiagramEnum.bali_paderborn);
    addDiagram(DiagramEnum.bali_lastyear);
  }


  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.bali_diagram_activity_menu, menu);
    return super.onCreateOptionsMenu(menu);
  }


  @Override
  protected boolean onCustomItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_bali_7days:
        viewPager.setCurrentItem(0, true);
        break;
      case R.id.action_bali_wind:
        viewPager.setCurrentItem(1, true);
        return true;
      case R.id.action_bali_humidity:
        viewPager.setCurrentItem(2, true);
        return true;
      case R.id.action_30_days:
        viewPager.setCurrentItem(3, true);
        return true;
      case R.id.action_bali_paderborn:
        viewPager.setCurrentItem(4, true);
        return true;
      case R.id.action_last_year:
        viewPager.setCurrentItem(5, true);
        return true;
    }
    return false;
  }
}
