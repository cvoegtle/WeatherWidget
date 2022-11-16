package org.voegtle.weatherwidget.util

import android.content.Context
import android.os.AsyncTask
import android.widget.Toast

class NotificationTask(private val context: Context, private val message: String) : AsyncTask<Void, Void, Void>() {

  @Deprecated("Deprecated in Java")
  override fun doInBackground(vararg voids: Void): Void? = null

  @Deprecated("Deprecated in Java")
  override fun onPostExecute(aVoid: Void?) {
    val duration = Toast.LENGTH_SHORT
    val toast = Toast.makeText(context, message, duration)
    toast.show()
  }
}
