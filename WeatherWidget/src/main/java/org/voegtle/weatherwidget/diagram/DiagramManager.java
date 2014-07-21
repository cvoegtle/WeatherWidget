package org.voegtle.weatherwidget.diagram;

import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ImageView;
import org.voegtle.weatherwidget.DiagramActivity;
import org.voegtle.weatherwidget.R;
import org.voegtle.weatherwidget.persistence.DiagramCache;
import org.voegtle.weatherwidget.util.UserFeedback;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DiagramManager {

  private DiagramActivity activity;
  private DiagramMap diagrams = new DiagramMap();
  private DiagramEnum currentDiagram;
  private DiagramCache diagramCache;

  public DiagramManager(DiagramActivity activity) {

    this.activity = activity;
    this.diagramCache = new DiagramCache(activity);
  }

  public void onCreate() {
    diagramCache.readAll(diagrams);
    currentDiagram = diagramCache.readCurrentDiagram();
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

  public void updateDiagram() {
    if (currentDiagram == null) {
      currentDiagram = DiagramEnum.temperature7days;
    }
    updateDiagram(currentDiagram);
  }

  public void reloadDiagram() {
    if (currentDiagram != null) {
      updateDiagram(currentDiagram, true);
    }
  }

  private boolean inProgress;

  private void updateWeatherDiagram(DiagramEnum diagramId, boolean force) {
    if (inProgress) {
      return;
    }
    try {
      inProgress = true;

      currentDiagram = diagramId;
      diagramCache.saveCurrentDiagram(diagramId);
      Diagram diagram = diagrams.get(diagramId);
      if (diagram == null || isOld(diagram) || force) {
        Drawable image = new DiagramFetcher().fetchImageFromUrl(diagramId.getUrl());
        if (image == null) {
          Log.e(DiagramManager.class.getName(), "Fetching Image " + diagramId + " failed");
          new UserFeedback(activity).showMessage(R.string.message_diagram_update_failed);
          return;
        }
        diagram = new Diagram(diagramId, image);
        diagrams.put(diagramId, diagram);
        diagramCache.write(diagram);
      }
      showDiagram(diagram);
    } finally {
      inProgress = false;
    }
  }

  private void showDiagram(Diagram diagram) {
    final Drawable newImage = diagram.getImage();
    activity.runOnUiThread(new Runnable() {
      @Override
      public void run() {
        ImageView imageView = (ImageView) activity.findViewById(R.id.diagram_view);
        imageView.setImageDrawable(newImage);
      }
    });
  }

  private boolean isOld(Diagram diagram) {
    return (new Date().getTime() - diagram.getUpdateTimestamp().getTime()) > 60 * 60 * 1000;
  }

}
