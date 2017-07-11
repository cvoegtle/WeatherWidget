package uk.co.senab.photoview

import android.view.GestureDetector
import android.view.MotionEvent

/**
 * Provided default implementation of GestureDetector.OnDoubleTapListener, to be overridden with custom behavior,
 * if needed
 *
 */
class DefaultOnDoubleTapListener(val photoViewAttacher: PhotoViewAttacher) : GestureDetector.OnDoubleTapListener {

  override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
    return false
  }

  override fun onDoubleTap(ev: MotionEvent): Boolean {
    try {
      val scale = photoViewAttacher.getScale()
      val x = ev.x
      val y = ev.y

      when  {
        scale < photoViewAttacher.getMediumScale() -> photoViewAttacher.setScale(photoViewAttacher.getMediumScale(), x, y)
        scale < photoViewAttacher.getMaximumScale() -> photoViewAttacher.setScale(photoViewAttacher.getMaximumScale(), x, y)
        else -> photoViewAttacher.setScale(photoViewAttacher.getMinimumScale(), x, y)
      }
    } catch (e: ArrayIndexOutOfBoundsException) {
      // Can sometimes happen when getX() and getY() is called
    }

    return true
  }

  override fun onDoubleTapEvent(e: MotionEvent): Boolean {
    // Wait for the confirmed onDoubleTap() instead
    return false
  }

}
