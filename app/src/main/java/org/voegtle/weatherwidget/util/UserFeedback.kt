package org.voegtle.weatherwidget.util


import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast

class UserFeedback(private val context: Context) {

  fun showMessage(messageId: Int, notifyUser: Boolean) {
    if (notifyUser) {
      showMessage(messageId)
    }
  }

  fun showMessage(messageId: Int) {
    val message = context.resources.getString(messageId)

    val handler = Handler(Looper.getMainLooper())
    handler.post( {
      val toast = Toast.makeText(context, message, Toast.LENGTH_SHORT)
      toast.show()
    })
  }
}
