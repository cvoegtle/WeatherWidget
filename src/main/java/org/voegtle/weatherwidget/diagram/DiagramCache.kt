package org.voegtle.weatherwidget.diagram

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.util.*

internal class DiagramCache(private val context: Context) {
  val diagramPreferences: SharedPreferences = context.getSharedPreferences(DIAGRAM_CACHE, 0)

  companion object {
    private val DIAGRAM_CACHE = "DIAGRAM_CACHE"
    private val DIAGRAM_AGE = "_AGE"
    private val CURRENT_DIAGRAM = "CURRENT_DIAGRAM"
  }

  fun saveCurrentDiagram(identifier: String, currentIndex: Int) {
    val editor = diagramPreferences.edit()
    editor.putInt(CURRENT_DIAGRAM + identifier, currentIndex)
    editor.apply()
  }

  fun readCurrentDiagram(identifier: String): Int {
    val diagramIndex = diagramPreferences.getInt(CURRENT_DIAGRAM + identifier, -1)
    return if (diagramIndex > 0) diagramIndex else 0
  }

  fun read(diagramId: DiagramEnum): Diagram? {
    val age = diagramPreferences.getLong(getAgeKey(diagramId), -1)
    return if (age > 0) readInternal(diagramId, age) else null
  }

  fun readInternal(diagramId: DiagramEnum, age: Long): Diagram {
    context.openFileInput(diagramId.filename).use {
      val image = Drawable.createFromStream(it, "Local Cache")
      return Diagram(diagramId, image, Date(age))
    }
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
    editor.apply()

    saveAsPngFile(diagram)
  }

  private fun saveAsPngFile(diagram: Diagram) {
    context.openFileOutput(diagram.id.filename, Context.MODE_PRIVATE).use {
      saveDrawableAsPng(diagram.image, it)
    }
  }

  private fun saveDrawableAsPng(image: Drawable, outputStream: OutputStream) {
    val bitmap = (image as BitmapDrawable).bitmap
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
  }

  private fun getAgeKey(diagramId: DiagramEnum): String {
    return "$diagramId$DIAGRAM_AGE"
  }
}
