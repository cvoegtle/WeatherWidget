package uk.co.senab.photoview

import android.view.GestureDetector
import android.view.MotionEvent

/**
 * Provided default implementation of GestureDetector.OnDoubleTapListener, to be overriden with custom behavior, if needed
 *
 * &nbsp;
 * To be used via [uk.co.senab.photoview.PhotoViewAttacher.setOnDoubleTapListener]
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

      if (scale < photoViewAttacher.getMediumScale()) {
        photoViewAttacher.setScale(photoViewAttacher.getMediumScale(), x, y, true)
      } else if (scale >= photoViewAttacher.getMediumScale() && scale < photoViewAttacher.getMaximumScale()) {
        photoViewAttacher.setScale(photoViewAttacher.getMaximumScale(), x, y, true)
      } else {
        photoViewAttacher.setScale(photoViewAttacher.getMinimumScale(), x, y, true)
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
