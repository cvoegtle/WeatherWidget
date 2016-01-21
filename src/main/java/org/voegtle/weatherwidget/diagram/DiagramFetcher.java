package org.voegtle.weatherwidget.diagram;

import android.graphics.drawable.Drawable;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class DiagramFetcher {

  public DiagramFetcher() {
  }

  private int COMMUNICATION_TIMEOUT = 60000;

  public Drawable fetchImageFromUrl(DiagramEnum diagramId) {
    Drawable image = null;
    try {
      URL url = new URL(diagramId.getUrl());
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setConnectTimeout(COMMUNICATION_TIMEOUT);
      connection.setReadTimeout(COMMUNICATION_TIMEOUT);
      InputStream input = connection.getInputStream();
      try {
        image = createImageFromResponse(input);
      } finally {
        input.close();
        connection.disconnect();
      }
    } catch (Throwable e) {
      Log.e(DiagramFetcher.class.toString(), "Failed to download image", e);
    }
    return image;
  }

  private Drawable createImageFromResponse(InputStream inputStream) throws IOException {
    return Drawable.createFromStream(inputStream, "Google Drive");
  }
}
