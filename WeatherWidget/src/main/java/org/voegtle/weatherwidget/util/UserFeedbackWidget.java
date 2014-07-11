package org.voegtle.weatherwidget.util;

import android.content.Context;
import android.content.res.Resources;
import android.widget.Toast;


public class UserFeedbackWidget {
  private final Context context;
  private final Resources res;

  public UserFeedbackWidget(Context context, Resources res) {
    this.context = context;
    this.res = res;
  }

  public void showMessage(int messageId) {
    final String message = res.getString(messageId);

    int duration = Toast.LENGTH_SHORT;
    Toast toast = Toast.makeText(context, message, duration);
    toast.show();
  }

}
