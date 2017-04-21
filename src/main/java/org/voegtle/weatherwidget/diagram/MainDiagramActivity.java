package org.voegtle.weatherwidget.diagram;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import org.voegtle.weatherwidget.R;

public class MainDiagramActivity extends DiagramActivity {


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    addDiagram(DiagramEnum.temperature7days);
    addDiagram(DiagramEnum.pb_bali_leo);
    addDiagram(DiagramEnum.family_weather);
    addDiagram(DiagramEnum.average7days);
    addDiagram(DiagramEnum.rain);
    addDiagram(DiagramEnum.summerdays);
    addDiagram(DiagramEnum.summerdays2016);
    addDiagram(DiagramEnum.winterdays);
  }


  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.diagram_activity_menu, menu);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  protected boolean onCustomItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_7_days:
        viewPager.setCurrentItem(0, true);
        return true;

      case R.id.action_7_days_owl:
        viewPager.setCurrentItem(1, true);
        return true;

      case R.id.action_family_weather:
        viewPager.setCurrentItem(2, true);
        return true;

      case R.id.action_7_days_average:
        viewPager.setCurrentItem(3, true);
        return true;

      case R.id.action_rain:
        viewPager.setCurrentItem(4, true);
        break;

      case R.id.action_summerdays:
        viewPager.setCurrentItem(5, true);
        return true;

      case R.id.action_summerdays2016:
        viewPager.setCurrentItem(6, true);
        return true;


      case R.id.action_winterdays:
        viewPager.setCurrentItem(7, true);
        return true;
    }
    return false;
  }

}
