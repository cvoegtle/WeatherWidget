package org.voegtle.weatherwidget.util;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

public class NotificationTask extends AsyncTask<Void, Void, Void> {
  private String message;
  private Context context;

  public NotificationTask(Context context, String message) {
    this.message = message;
    this.context = context;
  }

  @Override
  protected Void doInBackground(Void... voids) {
    return null;
  }

  @Override
  protected void onPostExecute(Void aVoid) {
    int duration = Toast.LENGTH_SHORT;
    Toast toast = Toast.makeText(context, message, duration);
    toast.show();

  }
}
