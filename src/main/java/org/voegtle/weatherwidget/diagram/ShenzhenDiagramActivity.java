package org.voegtle.weatherwidget.diagram;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import org.voegtle.weatherwidget.R;

public class ShenzhenDiagramActivity extends DiagramActivity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    addDiagram(DiagramEnum.shenzhen_7days);
    addDiagram(DiagramEnum.shenzhen_30days);
    addDiagram(DiagramEnum.shenzhen_lastyear);
  }


  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.shenzhen_diagram_activity_menu, menu);
    return super.onCreateOptionsMenu(menu);
  }


  @Override
  protected boolean onCustomItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_7_days:
        viewPager.setCurrentItem(0, true);
        return true;
      case R.id.action_30_days:
        viewPager.setCurrentItem(1, true);
        return true;
      case R.id.action_last_year:
        viewPager.setCurrentItem(2, true);
        return true;
    }
    return false;
  }
}
