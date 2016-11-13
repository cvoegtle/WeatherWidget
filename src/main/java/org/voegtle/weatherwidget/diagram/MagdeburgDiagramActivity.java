package org.voegtle.weatherwidget.diagram;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import org.voegtle.weatherwidget.R;
import org.voegtle.weatherwidget.util.StringUtil;

public class MagdeburgDiagramActivity extends DiagramActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    addDiagram(DiagramEnum.magdeburg_regen);
    addDiagram(DiagramEnum.magdeburg_humidity);
    addDiagram(DiagramEnum.magdeburg_paderborn_freiburg);
    addDiagram(DiagramEnum.magedburg_30days);
    addDiagram(DiagramEnum.magedburg_lastyear);
    Intent intent = getIntent();
    if (intent != null) {
      String title = intent.getStringExtra(MagdeburgDiagramActivity.class.getName());
      if (StringUtil.isNotEmpty(title)) {
        setTitle(title);
      }
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.magdeburg_diagram_activity_menu, menu);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  protected boolean onCustomItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_magdeburg_rain:
        viewPager.setCurrentItem(0, true);
        return true;
      case R.id.action_magdeburg_humidity:
        viewPager.setCurrentItem(1, true);
        return true;
      case R.id.action_md_pb_fr:
        viewPager.setCurrentItem(2, true);
        return true;
      case R.id.action_md_30days:
        viewPager.setCurrentItem(3, true);
        return true;
      case R.id.action_md_last_year:
        viewPager.setCurrentItem(4, true);
        return true;
    }
    return false;
  }
}
