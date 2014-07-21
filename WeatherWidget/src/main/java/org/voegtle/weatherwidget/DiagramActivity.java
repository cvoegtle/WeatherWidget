package org.voegtle.weatherwidget;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import org.voegtle.weatherwidget.diagram.DiagramEnum;
import org.voegtle.weatherwidget.diagram.DiagramManager;

public class DiagramActivity extends Activity {
  private DiagramManager diagramManager;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_digrams);
    this.diagramManager = new DiagramManager(this);
    this.diagramManager.onCreate();
    diagramManager.updateDiagram(DiagramEnum.temperature7days);
  }

  @Override
  protected void onResume() {
    super.onResume();
    diagramManager.updateDiagram();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.diagram_activity_menu, menu);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_reload:
        return true;
      case R.id.action_7_days:
        diagramManager.updateDiagram(DiagramEnum.temperature7days);
        return true;

      case R.id.action_7_days_average:
        diagramManager.updateDiagram(DiagramEnum.average7days);
        return true;

      case R.id.action_summerdays:
        diagramManager.updateDiagram(DiagramEnum.summerdays);
        return true;
    }
    return false;
  }


}
