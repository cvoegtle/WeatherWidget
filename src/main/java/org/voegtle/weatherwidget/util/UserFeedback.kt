package org.voegtle.weatherwidget.util


import android.app.Activity
import android.content.Context
import android.widget.Toast

class UserFeedback(private val activity: Activity) {

  fun showMessage(messageId: Int, notifyUser: Boolean) {
    if (notifyUser) {
      showMessage(messageId)
    }
  }

  fun showMessage(messageId: Int) {
    val message = activity.resources.getString(messageId)
    val context = activity.applicationContext

    activity.runOnUiThread {
      val duration = Toast.LENGTH_SHORT
      val toast = Toast.makeText(context, message, duration)
      toast.show()
    }
  }
}
