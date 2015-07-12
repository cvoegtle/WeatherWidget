package org.voegtle.weatherwidget.diagram;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import org.voegtle.weatherwidget.R;
import org.voegtle.weatherwidget.preferences.ApplicationSettings;
import org.voegtle.weatherwidget.preferences.ColorScheme;
import org.voegtle.weatherwidget.preferences.WeatherSettingsReader;
import org.voegtle.weatherwidget.util.UserFeedback;
import uk.co.senab.photoview.PhotoViewAttacher;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DiagramManager {

  private DiagramFragment fragment;
  private DiagramCache diagramCache;
  private Drawable placeholderImage;
  private boolean active = false;

  public DiagramManager(DiagramFragment diagramFragment) {
    this.fragment = diagramFragment;
    this.diagramCache = new DiagramCache(fragment.getActivity());
  }

  public void onResume() {
    configureTheme();

    active = true;
  }

  private void configureTheme() {
    Context context = fragment.getActivity().getApplicationContext();
    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
    WeatherSettingsReader weatherSettingsReader = new WeatherSettingsReader(context);
    ApplicationSettings configuration = weatherSettingsReader.read(preferences);
    if (configuration.getColorScheme() == ColorScheme.dark) {
      placeholderImage = ContextCompat.getDrawable(context, R.drawable.ic_action_picture);
    } else {
      placeholderImage = ContextCompat.getDrawable(context, R.drawable.ic_action_picture_dark);
    }
  }

  public void onPause() {
    active = false;
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

      Diagram diagram = diagramCache.read(diagramId);
      if (diagram == null || diagram.isOld() || force) {
        showDrawable(placeholderImage);
        Drawable image = fetchDrawable(diagramId);
        diagram = new Diagram(diagramId, image);

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
    if (active) {
      fragment.getActivity().runOnUiThread(new Runnable() {
        @Override
        public void run() {
          View view = fragment.getView();
          if (view != null) {
            ImageView imageView = (ImageView) view.findViewById(R.id.diagram_view);
            imageView.setImageDrawable(newImage);
            PhotoViewAttacher attacher = new PhotoViewAttacher(imageView);
            attacher.update();
          } else {
            Log.e("DiagramManager", "View is null");
          }
        }
      });
    }
  }

}
