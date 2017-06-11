package org.voegtle.weatherwidget.diagram

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.Log

import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.Date

internal class DiagramCache(private val context: Context) {
  var diagramPreferences: SharedPreferences

  init {
    diagramPreferences = context.getSharedPreferences(DIAGRAM_CACHE, 0)
  }

  fun saveCurrentDiagram(identifer: String, currentIndex: Int) {
    val editor = diagramPreferences.edit()
    editor.putInt(CURRENT_DIAGRAM + identifer, currentIndex)
    editor.commit()
  }

  fun readCurrentDiagram(identifier: String): Int {
    val diagramIndex = diagramPreferences.getInt(CURRENT_DIAGRAM + identifier, -1)
    return if (diagramIndex > 0) diagramIndex else 0
  }

  fun read(diagramId: DiagramEnum): Diagram? {
    val age = diagramPreferences.getLong(getAgeKey(diagramId), -1)
    if (age > 0) {
      try {
        return readInternal(diagramId, age)
      } catch (ex: Throwable) {
        Log.e(DiagramCache::class.java.name, "failed to read file " + diagramId.filename, ex)
      }
    }
    return null
  }

  fun readInternal(diagramId: DiagramEnum, age: Long): Diagram {
    val filename = diagramId.filename
    val inputStream = context.openFileInput(filename)
    val image = Drawable.createFromStream(inputStream, "Local Cache")
    inputStream.close()
    return Diagram(diagramId, image, Date(age))
  }

  fun asPNG(diagramId: DiagramEnum): ByteArray {
    val diagram = readInternal(diagramId, 1)
    val bytes = ByteArrayOutputStream()
    saveDrawableAsPng(diagram.image, bytes)
    return bytes.toByteArray()
  }


  fun write(diagram: Diagram) {
    val editor = diagramPreferences.edit()
    editor.putLong(getAgeKey(diagram.id), diagram.updateTimestamp.time)
    editor.commit()

    saveAsPngFile(diagram)
  }

  private fun saveAsPngFile(diagram: Diagram) {
    try {
      val outputStream = context.openFileOutput(diagram.id.filename, Context.MODE_PRIVATE)
      saveDrawableAsPng(diagram.image, outputStream)
      outputStream.close()
    } catch (ex: IOException) {
      Log.e(DiagramCache::class.java.name, "failed to write Diagram " + diagram.id)
    }

  }

  private fun saveDrawableAsPng(image: Drawable, outputStream: OutputStream) {
    val bitmap = (image as BitmapDrawable).bitmap
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
  }

  private fun getAgeKey(diagramId: DiagramEnum): String {
    return diagramId.toString() + DIAGRAM_AGE
  }

  companion object {
    private val DIAGRAM_CACHE = "DIAGRAM_CACHE"
    private val DIAGRAM_AGE = "_AGE"
    private val CURRENT_DIAGRAM = "CURRENT_DIAGRAM"
  }

}
