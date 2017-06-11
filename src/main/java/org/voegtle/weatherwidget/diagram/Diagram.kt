package org.voegtle.weatherwidget.diagram

import android.graphics.drawable.Drawable

import java.util.Date

class Diagram (val id: DiagramEnum, val image: Drawable, val updateTimestamp: Date) {
  constructor(id: DiagramEnum, image: Drawable) : this(id, image, Date())

  fun isOld(): Boolean {
    return Date().time - updateTimestamp.time > 60 * 60 * 1000
  }
}
