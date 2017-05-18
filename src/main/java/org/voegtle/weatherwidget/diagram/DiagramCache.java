package org.voegtle.weatherwidget.diagram;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

class DiagramCache {
  private static String DIAGRAM_CACHE = "DIAGRAM_CACHE";
  private static String DIAGRAM_AGE = "_AGE";
  private static String CURRENT_DIAGRAM = "CURRENT_DIAGRAM";
  SharedPreferences diagramPreferences;
  private Context context;

  DiagramCache(Context context) {
    this.context = context;
    diagramPreferences = context.getSharedPreferences(DIAGRAM_CACHE, 0);
  }

  void saveCurrentDiagram(String identifer, int currentIndex) {
    SharedPreferences.Editor editor = diagramPreferences.edit();
    editor.putInt(CURRENT_DIAGRAM + identifer, currentIndex);
    editor.commit();
  }

  int readCurrentDiagram(String identifier) {
    int diagramIndex = diagramPreferences.getInt(CURRENT_DIAGRAM + identifier, -1);
    return diagramIndex > 0 ? diagramIndex : 0;
  }

  Diagram read(DiagramEnum diagramId) {
    long age = diagramPreferences.getLong(getAgeKey(diagramId), -1);
    if (age > 0) {
      String filename = diagramId.getFilename();
      try {
        InputStream inputStream = context.openFileInput(filename);
        Drawable image = Drawable.createFromStream(inputStream, "Local Cache");
        inputStream.close();
        return new Diagram(diagramId, image, new Date(age));
      } catch (Throwable ex) {
        Log.e(DiagramCache.class.getName(), "failed to read file " + filename, ex);
      }
    }
    return null;
  }

  byte[] asPNG(DiagramEnum diagramId) {
    Diagram diagram = read(diagramId);
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    saveDrawableAsPng(diagram.getImage(), bytes);
    return bytes.toByteArray();
  }


  void write(Diagram diagram) {
    SharedPreferences.Editor editor = diagramPreferences.edit();
    editor.putLong(getAgeKey(diagram.getId()), diagram.getUpdateTimestamp().getTime());
    editor.commit();

    saveAsPngFile(diagram);
  }

  private void saveAsPngFile(Diagram diagram) {
    try {
      OutputStream outputStream = context.openFileOutput(diagram.getId().getFilename(), Context.MODE_PRIVATE);
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

}
