package org.voegtle.weatherwidget.diagram;

import android.graphics.drawable.Drawable;
import android.util.Log;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;

public class DiagramFetcher {
  private HttpClient client;

  public DiagramFetcher() {
    client = new DefaultHttpClient();
  }

  public Drawable fetchImageFromUrl(DiagramEnum diagramId) {
    HttpGet httpGet = new HttpGet(diagramId.getUrl());
    try {
      HttpResponse response = client.execute(httpGet);
      StatusLine statusLine = response.getStatusLine();
      int statusCode = statusLine.getStatusCode();
      if (statusCode / 100 == 2) { // check for 200 ... 299
        return createImageFromResponse(response);
      }
    } catch (Throwable e) {
      Log.e(DiagramFetcher.class.toString(), "Failed to download image", e);
    }
    return null;
  }

  private Drawable createImageFromResponse(HttpResponse response) throws IOException {
    HttpEntity entity = response.getEntity();
    InputStream inputStream = entity.getContent();
    return Drawable.createFromStream(inputStream, "Google Drive");
  }
}
