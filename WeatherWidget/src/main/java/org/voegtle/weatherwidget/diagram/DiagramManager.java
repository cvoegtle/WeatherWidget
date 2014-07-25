package org.voegtle.weatherwidget.diagram;

import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ImageView;
import org.voegtle.weatherwidget.R;
import org.voegtle.weatherwidget.util.UserFeedback;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DiagramManager {

  private DiagramFragment fragment;
  private DiagramMap diagrams = new DiagramMap();
  private DiagramCache diagramCache;
  private Drawable placeholderImage;

  public DiagramManager(DiagramFragment diagramFragment, DiagramCache diagramCache) {
    this.fragment = diagramFragment;
    this.diagramCache = diagramCache;
  }

  public void onResume() {
    placeholderImage = fragment.getResources().getDrawable(R.drawable.ic_action_picture_dark);
    diagramCache.readAll(diagrams);
  }

  public void updateDiagram(final DiagramEnum diagramId) {
    updateDiagram(diagramId, false);
  }

  public void updateDiagram(final DiagramEnum diagramId, final boolean force) {
    final Runnable updater = new Runnable() {
      @Override
      public void run() {
        updateWeatherDiagram(diagramId, force);
      }
    };
    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    scheduler.schedule(updater, 0, TimeUnit.SECONDS);
  }

  private boolean inProgress;

  private void updateWeatherDiagram(DiagramEnum diagramId, boolean force) {
    if (inProgress) {
      return;
    }
    try {
      inProgress = true;

      Diagram diagram = diagrams.get(diagramId);
      if (diagram == null || diagram.isOld() || force) {
        showDrawable(placeholderImage);
        Drawable image = fetchDrawable(diagramId);
        diagram = new Diagram(diagramId, image);

        diagrams.put(diagramId, diagram);
        diagramCache.write(diagram);
      }

      showDiagram(diagram);
    } finally {
      inProgress = false;
    }
  }

  private Drawable fetchDrawable(DiagramEnum diagramId) {
    Drawable image = new DiagramFetcher().fetchImageFromUrl(diagramId);
    if (image == null) {
      new UserFeedback(fragment.getActivity()).showMessage(R.string.message_diagram_update_failed);
      String message = "Fetching Image " + diagramId + " failed";
      Log.e(DiagramManager.class.getName(), message);
      throw new RuntimeException(message);
    }
    return image;
  }

  private void showDiagram(Diagram diagram) {
    final Drawable newImage = diagram.getImage();
    showDrawable(newImage);
  }

  private void showDrawable(final Drawable newImage) {
    fragment.getActivity().runOnUiThread(new Runnable() {
      @Override
      public void run() {
        ImageView imageView = (ImageView) fragment.getView().findViewById(R.id.diagram_view);
        imageView.setImageDrawable(newImage);
      }
    });
  }

}
