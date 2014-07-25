package org.voegtle.weatherwidget.diagram;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

public class DiagramCache {
  private static String DIAGRAM_CACHE = "DIAGRAM_CACHE";
  private static String DIAGRAM_AGE = "_AGE";
  private static String CURRENT_DIAGRAM = "CURRENT_DIAGRAM";
  SharedPreferences diagramPreferences;
  private Context context;

  public DiagramCache(Context context) {
    this.context = context;
    diagramPreferences = context.getSharedPreferences(DIAGRAM_CACHE, 0);
  }

  public void saveCurrentDiagram(int currentIndex) {
    SharedPreferences.Editor editor = diagramPreferences.edit();
    editor.putInt(CURRENT_DIAGRAM, currentIndex);
    editor.commit();
  }

  public int readCurrentDiagram() {
    int diagramIndex = diagramPreferences.getInt(CURRENT_DIAGRAM, -1);
    return diagramIndex > 0 ? diagramIndex : 0;
  }

  public void readAll(DiagramMap diagrams) {
    diagrams.clear();
    for (DiagramEnum diagramId : DiagramEnum.values()) {
      Diagram diagram = read(diagramId);
      if (diagram != null) {
        diagrams.put(diagramId, diagram);
      }
    }
  }

  private Diagram read(DiagramEnum diagramId) {
    long age = diagramPreferences.getLong(getAgeKey(diagramId), -1);
    if (age > 0) {
      String filename = getFilename(diagramId);
      try {
        InputStream inputStream = context.openFileInput(filename);
        Drawable image = Drawable.createFromStream(inputStream, "Local Cache");
        inputStream.close();
        return new Diagram(diagramId, image, new Date(age));
      } catch (IOException ex) {
        Log.e(DiagramCache.class.getName(), "failed to read file " + filename);
      }
    }
    return null;
  }

  public void write(Diagram diagram) {
    SharedPreferences.Editor editor = diagramPreferences.edit();
    editor.putLong(getAgeKey(diagram.getId()), diagram.getUpdateTimestamp().getTime());
    editor.commit();

    saveAsPngFile(diagram);
  }

  private void saveAsPngFile(Diagram diagram) {
    try {
      OutputStream outputStream = context.openFileOutput(getFilename(diagram.getId()), Context.MODE_PRIVATE);
      saveDrawableAsPng(diagram.getImage(), outputStream);
      outputStream.close();
    } catch (IOException ex) {
      Log.e(DiagramCache.class.getName(), "failed to write Diagram " + diagram.getId());
    }
  }

  private void saveDrawableAsPng(Drawable image, OutputStream outputStream) {
    Bitmap bitmap = ((BitmapDrawable) image).getBitmap();
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
  }

  private String getAgeKey(DiagramEnum diagramId) {
    return diagramId.toString() + DIAGRAM_AGE;
  }

  private String getFilename(DiagramEnum diagramId) {
    return diagramId.toString() + ".png";
  }

}
