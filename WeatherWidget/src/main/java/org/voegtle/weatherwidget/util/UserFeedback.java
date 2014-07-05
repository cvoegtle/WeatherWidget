package org.voegtle.weatherwidget.util;


import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

public class UserFeedback {
  private Activity activity;

  public UserFeedback(Activity activity) {
    this.activity = activity;
  }

  public void showMessage(int messageId, boolean notifyUser) {
    if (notifyUser) {
      final String message = activity.getResources().getString(messageId);
      final Context context = activity.getApplicationContext();

      activity.runOnUiThread(new Runnable() {
        @Override
        public void run() {
          int duration = Toast.LENGTH_SHORT;
          Toast toast = Toast.makeText(context, message, duration);
          toast.show();
        }
      });
    }
  }
}
