package org.voegtle.weatherwidget;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import org.voegtle.weatherwidget.diagram.DiagramEnum;
import org.voegtle.weatherwidget.diagram.DiagramManager;
import org.voegtle.weatherwidget.persistence.DiagramCache;

public class DiagramFragment extends Fragment {

  private DiagramManager diagramManager;
  private DiagramEnum diagramId;

  public DiagramFragment(DiagramCache diagramCache, DiagramEnum diagramId) {
    this.diagramManager = new DiagramManager(this, diagramCache);
    this.diagramManager.onCreate();
    this.diagramId = diagramId;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    return (ViewGroup) inflater.inflate(
        R.layout.fragment_diagram, container, false);
  }

  @Override
  public void onResume() {
    super.onResume();
    diagramManager.updateDiagram(diagramId);
  }

  public void reload() {
    diagramManager.updateDiagram(diagramId, true);
  }
}
