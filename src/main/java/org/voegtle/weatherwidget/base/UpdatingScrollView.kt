package org.voegtle.weatherwidget.base

import android.content.Context
import android.util.AttributeSet
import android.widget.ScrollView
import org.voegtle.weatherwidget.util.DateUtil

import java.util.Date

class UpdatingScrollView : ScrollView {
  interface Updater {
    fun update()
  }

  private var lastUpdate = Date()
  private var updater: Updater? = null

  constructor(context: Context) : super(context)

  constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

  constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

  fun register(updater: Updater) {
    this.updater = updater
  }


  override fun onOverScrolled(scrollX: Int, scrollY: Int, clampedX: Boolean, clampedY: Boolean) {
    super.onOverScrolled(scrollX, scrollY, clampedX, clampedY)
    if (updater != null) {
      if (scrollY == 0 && clampedY && DateUtil.getAge(lastUpdate) > 5) {
        lastUpdate = Date()
        updater!!.update()
      }
    }
  }
}
