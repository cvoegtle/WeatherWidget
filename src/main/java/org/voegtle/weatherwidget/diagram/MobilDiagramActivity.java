package org.voegtle.weatherwidget.diagram;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import org.voegtle.weatherwidget.R;
import org.voegtle.weatherwidget.util.StringUtil;

public class MobilDiagramActivity extends DiagramActivity {


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    addDiagram(DiagramEnum.mobil_paderborn);

    Intent intent = getIntent();
    if (intent != null) {
      String title = intent.getStringExtra(MobilDiagramActivity.class.getName());
      if (StringUtil.isNotEmpty(title)) {
        setTitle(title);
      }
    }
  }


  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.mobil_diagram_activity_menu, menu);
    return super.onCreateOptionsMenu(menu);
  }


  @Override
  protected boolean onCustomItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_instant_paderborn:
        viewPager.setCurrentItem(0, true);
        return true;
    }
    return false;
  }
}
