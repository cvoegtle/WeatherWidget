package org.voegtle.weatherwidget.diagram;

import android.graphics.drawable.Drawable;
import android.util.Log;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.InputStream;

public class DiagramFetcher {
  public DiagramFetcher() {
  }

  public Drawable fetchImageFromUrl(String url) {
    HttpClient client = new DefaultHttpClient();
    HttpGet httpGet = new HttpGet(url);
    try {
      HttpResponse response = client.execute(httpGet);
      StatusLine statusLine = response.getStatusLine();
      int statusCode = statusLine.getStatusCode();
      if (statusCode == 200) {
        HttpEntity entity = response.getEntity();
        InputStream inputStream = entity.getContent();
        Drawable image = Drawable.createFromStream(inputStream, "Google Drive");
        return image;
      }
    } catch (Exception e) {
      Log.d(DiagramFetcher.class.toString(), "Failed to download image", e);
    }
    return null;
  }
}
