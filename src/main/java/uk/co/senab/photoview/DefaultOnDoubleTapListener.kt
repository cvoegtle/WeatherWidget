package uk.co.senab.photoview

import android.view.GestureDetector
import android.view.MotionEvent
import uk.co.senab.photoview.data.FloatPosition

/**
 * Provided default implementation of GestureDetector.OnDoubleTapListener, to be overridden with custom behavior,
 * if needed
 *
 */
class DefaultOnDoubleTapListener(val photoView: PhotoView) : GestureDetector.OnDoubleTapListener {

  override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
    return false
  }

  override fun onDoubleTap(ev: MotionEvent): Boolean {
    try {
      val scale = photoView.getScale()
      val position = FloatPosition(ev.x, ev.y)

      when  {
        scale < photoView.MID_SCALE ->photoView.setScale(photoView.MID_SCALE, position)
        scale < photoView.MAX_SCALE -> photoView.setScale(photoView.MAX_SCALE, position)
        else -> photoView.setScale(photoView.MIN_SCALE, position)
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
