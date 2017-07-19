/**
 * ****************************************************************************
 * Copyright 2011, 2012 Chris Banes.
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * *****************************************************************************
 */
package uk.co.senab.photoview

import android.content.Context
import android.graphics.Matrix
import android.graphics.Matrix.ScaleToFit
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.MotionEvent.*
import android.view.View
import android.view.ViewTreeObserver
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Interpolator
import android.widget.ImageView
import android.widget.ImageView.ScaleType
import android.widget.OverScroller
import uk.co.senab.photoview.data.Edge
import uk.co.senab.photoview.data.FloatPosition
import uk.co.senab.photoview.data.IntPosition
import uk.co.senab.photoview.data.Rectangle
import uk.co.senab.photoview.gestures.IGestureDetector
import uk.co.senab.photoview.gestures.OnGestureListener
import uk.co.senab.photoview.gestures.PhotoGestureDetector
import java.lang.ref.WeakReference

class PhotoView(imageView: ImageView) : View.OnTouchListener, OnGestureListener, ViewTreeObserver.OnGlobalLayoutListener {
  private val LOG_TAG = "PhotoView"

  // let debug flag be dynamic, but still Proguard can be used to remove from
  // release builds
  private val DEBUG = Log.isLoggable(LOG_TAG, Log.DEBUG)

  internal val MAX_SCALE = 3.0f
  internal val MID_SCALE = 1.75f
  internal val MIN_SCALE = 1.0f
  val DEFAULT_ZOOM_DURATION = 200

  internal var ZOOM_DURATION = DEFAULT_ZOOM_DURATION

  private var weakImageView: WeakReference<ImageView>? = WeakReference(imageView)

  // Gesture Detectors
  private val gestureDetector: GestureDetector
  private val scaleDragDetector: IGestureDetector

  // These are set so we don't keep allocating them on the heap
  private val baseMatrix = Matrix()
  private val drawMatrix = Matrix()
    get() {
      field.set(baseMatrix)
      field.postConcat(suppMatrix)
      return field
    }

  private val suppMatrix = Matrix()
  private val displayRect = RectF()
  private val matrixValues = FloatArray(9)

  private var imageViewRect = Rectangle(0, 0, 0, 0)
  private var currentFlingRunnable: FlingRunnable? = null
  private var scrollEdge = Edge.BOTH

  init {
    imageView.isDrawingCacheEnabled = true
    imageView.setOnTouchListener(this)

    imageView.viewTreeObserver?.addOnGlobalLayoutListener(this)

    // Make sure we using MATRIX Scale Type
    imageView.scaleType = ScaleType.MATRIX

    // Create Gesture Detectors...
    scaleDragDetector = PhotoGestureDetector(imageView.context, this)

    gestureDetector = GestureDetector(imageView.context, GestureDetector.SimpleOnGestureListener())

    gestureDetector.setOnDoubleTapListener(DefaultOnDoubleTapListener(this))
    update()
  }

  /**
   * Clean-up the resources attached to this object. This needs to be called when the ImageView is
   * no longer used. A good example is from [android.view.View.onDetachedFromWindow] or
   * from [android.app.Activity.onDestroy].
   */
  fun cleanup() {
    if (null == weakImageView) {
      return  // cleanup already done
    }

    // Remove this as a global layout listener
    imageView?.viewTreeObserver?.removeGlobalOnLayoutListener(this)

    val imageView = weakImageView?.get()
    // Remove the ImageView's reference to this
    imageView?.setOnTouchListener(null)

    // make sure a pending fling runnable won't be run
    cancelFling()

    gestureDetector.setOnDoubleTapListener(null)

    // Finally, clear ImageView
    weakImageView = null
  }

  fun getDisplayRect(): RectF? {
    checkMatrixBounds()
    return getDisplayRect(drawMatrix)
  }

  // If we don't have an ImageView, call cleanup()
  val imageView: ImageView?
    get() {
      val imageView: ImageView? = weakImageView?.get()

      if (null == imageView) {
        cleanup()
        Log.i(LOG_TAG, "ImageView no longer exists. You should not use this PhotoViewAttacher any more.")
      }

      return imageView
    }

  fun getScale(): Float {
    return Math.sqrt(Math.pow(getValue(suppMatrix, Matrix.MSCALE_X).toDouble(), 2.0) + Math.pow(getValue(suppMatrix, Matrix.MSKEW_Y).toDouble(), 2.0)).toFloat()
  }

  override fun onDrag(position: FloatPosition) {
    if (scaleDragDetector.isScaling()) {
      return  // Do not position if we are already scaling
    }

    if (DEBUG) {
      Log.d(LOG_TAG, String.format("onDrag: dx: %.2f. dy: %.2f", position.x, position.y))
    }

    suppMatrix.postTranslate(position.x, position.y)
    checkAndDisplayMatrix()

    /**
     * Here we decide whether to let the ImageView's parent to start taking
     * over the touch event.

     * First we check whether this function is enabled. We never want the
     * parent to take over if we're scaling. We then check the edge we're
     * on, and the direction of the scroll (i.e. if we're pulling against
     * the edge, aka 'overscrolling', let the parent take over).
     */
    imageView?.let {
      val parent = it.parent
      if (!scaleDragDetector.isScaling()) {
        if (scrollEdge == Edge.BOTH
            || scrollEdge == Edge.LEFT && position.x >= 1f
            || scrollEdge == Edge.RIGHT && position.x <= -1f) {
          parent.requestDisallowInterceptTouchEvent(false)
        }
      } else {
        parent.requestDisallowInterceptTouchEvent(true)
      }
    }
  }

  override fun onFling(start: FloatPosition, velocityX: Float, velocityY: Float) {
    if (DEBUG) {
      Log.d(LOG_TAG, "onFling. sX: ${start.y} sY: ${start.y} Vx: $velocityX Vy: $velocityY")
    }
    imageView?.let {
      val newFlingRunnable = FlingRunnable(it.context)
      newFlingRunnable.fling(getImageViewWidth(it), getImageViewHeight(it), velocityX.toInt(), velocityY.toInt())
      it.post(newFlingRunnable)
      currentFlingRunnable = newFlingRunnable
    }
  }

  override fun onGlobalLayout() {
    imageView?.let {
      val currentRectangle = Rectangle(top = it.top, right = it.right, bottom = it.bottom, left = it.left)

      /**
       * We need to check whether the ImageView's bounds have changed.
       * This would be easier if we targeted API 11+ as we could just use
       * View.OnLayoutChangeListener. Instead we have to replicate the
       * work, keeping track of the ImageView's bounds and then checking
       * if the values change.
       */
      if (currentRectangle != imageViewRect) {
        // Update our base matrix, as the bounds have changed
        updateBaseMatrix(it.drawable)

        // Update values as something has changed
        imageViewRect = currentRectangle
      }
    }
  }

  override fun onScale(scaleFactor: Float, focus: FloatPosition) {
    if (DEBUG) {
      Log.d(LOG_TAG, String.format("onScale: scale: %.2f. fX: %.2f. fY: %.2f", scaleFactor, focus.x, focus.y))
    }

    if (getScale() < MAX_SCALE || scaleFactor < 1f) {
      suppMatrix.postScale(scaleFactor, scaleFactor, focus.x, focus.y)
      checkAndDisplayMatrix()
    }
  }

  override fun onTouch(v: View, ev: MotionEvent): Boolean {
    var handled = false

    if (hasDrawable(v as ImageView)) {
      val parent = v.getParent()
      when (ev.action) {
        ACTION_DOWN -> {
          // First, disable the Parent from intercepting the touch
          // event
          parent?.requestDisallowInterceptTouchEvent(true) ?: Log.i(LOG_TAG, "onTouch getParent() returned null")

          // If we're flinging, and the user presses down, cancel
          // fling
          cancelFling()
        }

        ACTION_CANCEL, ACTION_UP ->
          // If the user has zoomed less than min scale, zoom back
          // to min scale
          if (getScale() < MIN_SCALE) {
            val rect = getDisplayRect()
            if (null != rect) {
              v.post(AnimatedZoomRunnable(getScale(), MIN_SCALE, FloatPosition(rect.centerX(), rect.centerY())))
              handled = true
            }
          }
      }

      // Try the Scale/Drag detector
      if (scaleDragDetector.onTouchEvent(ev)) {
        handled = true
      }

      // Check to see if the user double tapped
      if (gestureDetector.onTouchEvent(ev)) {
        handled = true
      }
    }

    return handled
  }

  fun setScale(scale: Float, position: FloatPosition) {
    imageView?.let {
      // Check to see if the scale is within bounds
      if (scale < MIN_SCALE || scale > MAX_SCALE) {
        Log.i(LOG_TAG, "Scale must be within the range of minScale and maxScale")
        return
      }

      it.post(AnimatedZoomRunnable(getScale(), scale, position))
    }
  }

  fun update() {
    imageView?.let {
      // Make sure we using MATRIX Scale Type
      it.scaleType = ScaleType.MATRIX

      // Update the base matrix using the current drawable
      updateBaseMatrix(it.drawable)
    }
  }

  private fun cancelFling() {
    currentFlingRunnable?.cancelFling()
    currentFlingRunnable = null
  }

  /**
   * Helper method that simply checks the Matrix, and then displays the result
   */
  private fun checkAndDisplayMatrix() {
    if (checkMatrixBounds()) {
      setImageViewMatrix(drawMatrix)
    }
  }

  private fun checkImageViewScaleType() {
    /**
     * PhotoView's getScaleType() will just divert to this.getScaleType() so
     * only call if we're not attached to a PhotoView.
     */
    imageView?.let {
      if (ScaleType.MATRIX != it.scaleType) {
        throw IllegalStateException(
            "The ImageView's ScaleType has been changed since attaching a PhotoView")
      }
    }
  }

  private fun checkMatrixBounds(): Boolean {
    val imageView = imageView ?: return false

    val rect = getDisplayRect(drawMatrix) ?: return false

    val viewHeight = getImageViewHeight(imageView)
    val viewWidth = getImageViewWidth(imageView)

    val deltaY = when {
      rect.height() <= viewHeight -> (viewHeight - rect.height()) / 2 - rect.top
      rect.top > 0 -> -rect.top
      rect.bottom < viewHeight -> viewHeight - rect.bottom
      else -> 0f
    }

    val deltaX = when {
      rect.width() <= viewWidth -> {
        scrollEdge = Edge.BOTH
        (viewWidth - rect.width()) / 2 - rect.left
      }

      rect.left > 0 -> {
        scrollEdge = Edge.LEFT
        -rect.left
      }

      rect.right < viewWidth -> {
        scrollEdge = Edge.RIGHT
        viewWidth - rect.right
      }

      else -> {
        scrollEdge = Edge.NONE
        0f
      }
    }

    // Finally actually translate the matrix
    suppMatrix.postTranslate(deltaX, deltaY)
    return true
  }

  /**
   * Helper method that maps the supplied Matrix to the current Drawable

   * @param matrix - Matrix to map Drawable against
   * *
   * @return RectF - Displayed Rectangle
   */
  private fun getDisplayRect(matrix: Matrix): RectF? {
    imageView?.drawable?.let {
      displayRect.set(0f, 0f, it.intrinsicWidth.toFloat(), it.intrinsicHeight.toFloat())
      matrix.mapRect(displayRect)
      return displayRect
    }
    return null
  }

  /**
   * Helper method that 'unpacks' a Matrix and returns the required value

   * @param matrix     - Matrix to unpack
   * *
   * @param whichValue - Which value from Matrix.M* to return
   * *
   * @return float - returned value
   */
  private fun getValue(matrix: Matrix, whichValue: Int): Float {
    matrix.getValues(matrixValues)
    return matrixValues[whichValue]
  }

  /**
   * Resets the Matrix back to FIT_CENTER, and then displays it.s
   */
  private fun resetMatrix() {
    suppMatrix.reset()
    setImageViewMatrix(drawMatrix)
    checkMatrixBounds()
  }

  private fun setImageViewMatrix(matrix: Matrix) {
    imageView?.let {
      checkImageViewScaleType()
      it.imageMatrix = matrix
    }
  }

  /**
   * Calculate Matrix for FIT_CENTER

   * @param d - Drawable being displayed
   */
  private fun updateBaseMatrix(d: Drawable?) {
    val imageView = imageView
    if (null == imageView || null == d) {
      return
    }


    baseMatrix.reset()

    val drawableWidth = d.intrinsicWidth
    val drawableHeight = d.intrinsicHeight
    val sourceRectangle = RectF(0f, 0f, drawableWidth.toFloat(), drawableHeight.toFloat())

    val viewWidth = getImageViewWidth(imageView).toFloat()
    val viewHeight = getImageViewHeight(imageView).toFloat()
    val destinationRectangle = RectF(0f, 0f, viewWidth, viewHeight)

    baseMatrix.setRectToRect(sourceRectangle, destinationRectangle, ScaleToFit.CENTER)

    resetMatrix()
  }

  private fun getImageViewWidth(imageView: ImageView): Int {
    return imageView.width - imageView.paddingLeft - imageView.paddingRight
  }

  private fun getImageViewHeight(imageView: ImageView): Int {
    return imageView.height - imageView.paddingTop - imageView.paddingBottom
  }

  private inner class AnimatedZoomRunnable(private val mZoomStart: Float, private val mZoomEnd: Float,
                                           private val focal: FloatPosition) : Runnable {
    private val mStartTime: Long = System.currentTimeMillis()

    override fun run() {
      val imageView = imageView ?: return

      val t = interpolate()
      val scale = mZoomStart + t * (mZoomEnd - mZoomStart)
      val deltaScale = scale / getScale()

      suppMatrix.postScale(deltaScale, deltaScale, focal.x, focal.y)
      checkAndDisplayMatrix()

      // We haven't hit our target scale yet, so post ourselves again
      if (t < 1f) {
        Compat.postOnAnimation(imageView, this)
      }
    }

    private fun interpolate(): Float {
      var t = 1f * (System.currentTimeMillis() - mStartTime) / ZOOM_DURATION
      t = Math.min(1f, t)
      t = interpolator.getInterpolation(t)
      return t
    }
  }

  private inner class FlingRunnable(context: Context) : Runnable {

    private val scroller: OverScroller = OverScroller(context)
    private var currentPosition = IntPosition(0, 0)

    fun cancelFling() {
      if (DEBUG) {
        Log.d(LOG_TAG, "Cancel Fling")
      }
      scroller.forceFinished(true)
    }

    fun fling(viewWidth: Int, viewHeight: Int, velocityX: Int, velocityY: Int) {
      val rect = getDisplayRect() ?: return

      val startX = Math.round(-rect.left)
      val minX: Int
      val maxX: Int
      val minY: Int
      val maxY: Int

      if (viewWidth < rect.width()) {
        minX = 0
        maxX = Math.round(rect.width() - viewWidth)
      } else {
        maxX = startX
        minX = maxX
      }

      val startY = Math.round(-rect.top)
      if (viewHeight < rect.height()) {
        minY = 0
        maxY = Math.round(rect.height() - viewHeight)
      } else {
        maxY = startY
        minY = maxY
      }

      currentPosition = IntPosition(startX, startY)

      if (DEBUG) {
        Log.d(LOG_TAG, "fling. StartX: $startX StartY: $startY MaxX: $maxX MaxY: $maxY")
      }

      // If we actually can move, fling the scroller
      if (startX != maxX || startY != maxY) {
        scroller.fling(startX, startY, velocityX, velocityY, minX,
            maxX, minY, maxY, 0, 0)
      }
    }

    override fun run() {
      if (scroller.isFinished) {
        return  // remaining post that should not be handled
      }

      val imageView = imageView
      if (null != imageView && scroller.computeScrollOffset()) {

        val newPosition = IntPosition(scroller.currX, scroller.currY)

        if (DEBUG) {
          Log.d(LOG_TAG, "fling run(). ${currentPosition} $newPosition")
        }

        suppMatrix.postTranslate((currentPosition.x - newPosition.x).toFloat(),
            (currentPosition.y - newPosition.y).toFloat())
        setImageViewMatrix(drawMatrix)

        currentPosition = newPosition

        // Post On animation
        Compat.postOnAnimation(imageView, this)
      }
    }
  }

  /**
   * @return true if the ImageView's Drawable exists
   */
  private fun hasDrawable(imageView: ImageView): Boolean {
    return null != imageView.drawable
  }


  companion object {
    internal val interpolator: Interpolator = AccelerateDecelerateInterpolator()
  }
}
