package org.voegtle.weatherwidget.diagram;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import org.voegtle.weatherwidget.R;

public class DiagramFragment extends Fragment {

  private DiagramManager diagramManager = null;
  private DiagramEnum diagramId = null;

  public static DiagramFragment newInstance(DiagramEnum diagramId) {
    DiagramFragment newFragment = new DiagramFragment();

    Bundle bundle = new Bundle();
    bundle.putInt(DiagramEnum.class.getName(), diagramId.getId());
    newFragment.setArguments(bundle);

    return newFragment;
  }

  public DiagramFragment() {
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ensureResources();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_diagram, container, false);
  }

  @Override
  public void onResume() {
    super.onResume();
    ensureResources();
    diagramManager.onResume();
    diagramManager.updateDiagram(diagramId);
  }

  @Override
  public void onPause() {
    diagramManager.onPause();
    super.onPause();
  }

  private void ensureResources() {
    if (diagramManager == null) {
      this.diagramManager = new DiagramManager(this);
      diagramId = DiagramEnum.byId(getArguments().getInt(DiagramEnum.class.getName(), -1));
    }
  }


  public void reload() {
    diagramManager.updateDiagram(diagramId, true);
  }
}
