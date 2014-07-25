package org.voegtle.weatherwidget.diagram;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import org.voegtle.weatherwidget.R;

public class DiagramFragment extends Fragment {

  private DiagramManager diagramManager;
  private DiagramEnum diagramId;

  public DiagramFragment(DiagramCache diagramCache, DiagramEnum diagramId) {
    this.diagramManager = new DiagramManager(this, diagramCache);
    this.diagramId = diagramId;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_diagram, container, false);
  }

  @Override
  public void onResume() {
    super.onResume();
    diagramManager.onResume();
    diagramManager.updateDiagram(diagramId);
  }

  @Override
  public void onPause() {
    diagramManager.onPause();
    super.onPause();
  }

  public void reload() {
    diagramManager.updateDiagram(diagramId, true);
  }
}
