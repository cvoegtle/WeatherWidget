package org.voegtle.weatherwidget.diagram;

import android.graphics.drawable.Drawable;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class DiagramFetcher {

  public DiagramFetcher() {
  }

  public Drawable fetchImageFromUrl(DiagramEnum diagramId) {
    Drawable image = null;
    try {
      URL url = new URL(diagramId.getUrl());
      URLConnection connection = url.openConnection();
      InputStream input = connection.getInputStream();

      image = createImageFromResponse(input);

      input.close();
    } catch (Throwable e) {
      Log.e(DiagramFetcher.class.toString(), "Failed to download image", e);
    }
    return image;
  }

  private Drawable createImageFromResponse(InputStream inputStream) throws IOException {
    return Drawable.createFromStream(inputStream, "Google Drive");
  }
}
