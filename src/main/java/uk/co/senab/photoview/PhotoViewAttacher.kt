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
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.Matrix.ScaleToFit
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.MotionEvent.*
import android.view.View
import android.view.View.OnLongClickListener
import android.view.ViewTreeObserver
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Interpolator
import android.widget.ImageView
import android.widget.ImageView.ScaleType
import uk.co.senab.photoview.gestures.OnGestureListener
import uk.co.senab.photoview.gestures.VersionedGestureDetector
import uk.co.senab.photoview.log.LogManager
import uk.co.senab.photoview.scrollerproxy.ScrollerProxy
import java.lang.ref.WeakReference

class PhotoViewAttacher(imageView: ImageView) : IPhotoView, View.OnTouchListener, OnGestureListener, ViewTreeObserver.OnGlobalLayoutListener {
  internal var ZOOM_DURATION = IPhotoView.DEFAULT_ZOOM_DURATION

  private var mMinScale = IPhotoView.DEFAULT_MIN_SCALE
  private var mMidScale = IPhotoView.DEFAULT_MID_SCALE
  private var mMaxScale = IPhotoView.DEFAULT_MAX_SCALE

  private var mAllowParentInterceptOnEdge = true

  private var mImageView: WeakReference<ImageView>? = null
  private var observer: ViewTreeObserver? = null

  // Gesture Detectors
  private var mGestureDetector: GestureDetector? = null
  private var mScaleDragDetector: uk.co.senab.photoview.gestures.GestureDetector? = null

  // These are set so we don't keep allocating them on the heap
  private val mBaseMatrix = Matrix()
  private val mDrawMatrix = Matrix()
  private val mSuppMatrix = Matrix()
  private val mDisplayRect = RectF()
  private val mMatrixValues = FloatArray(9)

  // Listeners
  private var mMatrixChangeListener: OnMatrixChangedListener? = null
  private var mPhotoTapListener: OnPhotoTapListener? = null
  private var mLongClickListener: OnLongClickListener? = null

  private var mIvTop: Int = 0
  private var mIvRight: Int = 0
  private var mIvBottom: Int = 0
  private var mIvLeft: Int = 0
  private var mCurrentFlingRunnable: FlingRunnable? = null
  private var mScrollEdge = EDGE_BOTH

  private var mZoomEnabled: Boolean = false
  private var mScaleType = ScaleType.FIT_CENTER

  init {
    mImageView = WeakReference(imageView)

    imageView.isDrawingCacheEnabled = true
    imageView.setOnTouchListener(this)

    observer = imageView.viewTreeObserver
    if (null != observer)
      observer!!.addOnGlobalLayoutListener(this)

    // Make sure we using MATRIX Scale Type
    setImageViewScaleTypeMatrix(imageView)

    if (!imageView.isInEditMode) {
      // Create Gesture Detectors...
      mScaleDragDetector = VersionedGestureDetector.newInstance(
          imageView.context, this)

      mGestureDetector = GestureDetector(imageView.context,
          object : GestureDetector.SimpleOnGestureListener() {

            // forward long click listener
            override fun onLongPress(e: MotionEvent) {
              if (null != mLongClickListener) {
                mLongClickListener!!.onLongClick(imageView)
              }
            }
          })

      mGestureDetector!!.setOnDoubleTapListener(DefaultOnDoubleTapListener(this))

      // Finally, update the UI so that we're zoomable
      setZoomable(true)
    }
  }

  override fun setOnDoubleTapListener(newOnDoubleTapListener: GestureDetector.OnDoubleTapListener?) {
    if (newOnDoubleTapListener != null)
      this.mGestureDetector!!.setOnDoubleTapListener(newOnDoubleTapListener)
    else
      this.mGestureDetector!!.setOnDoubleTapListener(DefaultOnDoubleTapListener(this))
  }

  override fun canZoom(): Boolean {
    return mZoomEnabled
  }

  /**
   * Clean-up the resources attached to this object. This needs to be called when the ImageView is
   * no longer used. A good example is from [android.view.View.onDetachedFromWindow] or
   * from [android.app.Activity.onDestroy]. This is automatically called if you are using
   * [uk.co.senab.photoview.PhotoView].
   */
  fun cleanup() {
    if (null == mImageView) {
      return  // cleanup already done
    }

    val imageView = mImageView!!.get()
    // Remove this as a global layout listener
    if (null != observer) {
      observer!!.removeGlobalOnLayoutListener(this)
      observer = null
    }


    if (null != imageView) {

      // Remove the ImageView's reference to this
      imageView.setOnTouchListener(null)

      // make sure a pending fling runnable won't be run
      cancelFling()
    }

    mGestureDetector?.setOnDoubleTapListener(null)

    // Clear listeners too
    mMatrixChangeListener = null
    mPhotoTapListener = null

    // Finally, clear ImageView
    mImageView = null
  }

  override fun getDisplayRect(): RectF? {
    checkMatrixBounds()
    return getDisplayRect(drawMatrix)
  }

  override fun setDisplayMatrix(finalMatrix: Matrix): Boolean {
    val imageView = imageView ?: return false

    if (null == imageView.drawable)
      return false

    mSuppMatrix.set(finalMatrix)
    setImageViewMatrix(drawMatrix)
    checkMatrixBounds()

    return true
  }


  @Deprecated("use {@link #setRotationTo(float)}")
  override fun setPhotoViewRotation(degrees: Float) {
    mSuppMatrix.setRotate(degrees % 360)
    checkAndDisplayMatrix()
  }

  override fun setRotationTo(degrees: Float) {
    mSuppMatrix.setRotate(degrees % 360)
    checkAndDisplayMatrix()
  }

  override fun setRotationBy(degrees: Float) {
    mSuppMatrix.postRotate(degrees % 360)
    checkAndDisplayMatrix()
  }

  // If we don't have an ImageView, call cleanup()
  val imageView: ImageView?
    get() {
      var imageView: ImageView? = null

      if (null != mImageView) {
        imageView = mImageView!!.get()
      }
      if (null == imageView) {
        cleanup()
        Log.i(LOG_TAG,
            "ImageView no longer exists. You should not use this PhotoViewAttacher any more.")
      }

      return imageView
    }

  @Deprecated("")
  override fun getMinScale(): Float {
    return  getMinimumScale()
  }

  override fun getMinimumScale(): Float {
    return mMinScale
  }

  @Deprecated("")
  override fun getMidScale(): Float {
    return getMediumScale()
  }

  override fun getMediumScale(): Float {
    return mMidScale
  }

  @Deprecated("")
  override fun getMaxScale(): Float {
    return getMaximumScale()
  }

  override fun getMaximumScale(): Float {
    return mMaxScale
  }

  override fun getScale(): Float {
    return Math.sqrt(Math.pow(getValue(mSuppMatrix, Matrix.MSCALE_X).toDouble(), 2.0) + Math.pow(getValue(mSuppMatrix, Matrix.MSKEW_Y).toDouble(), 2.0)).toFloat()
  }

  override fun getScaleType(): ScaleType {
    return mScaleType
  }

  override fun onDrag(dx: Float, dy: Float) {
    if (mScaleDragDetector!!.isScaling()) {
      return  // Do not drag if we are already scaling
    }

    if (DEBUG) {
      LogManager.logger.d(LOG_TAG,
          String.format("onDrag: dx: %.2f. dy: %.2f", dx, dy))
    }

    val imageView = imageView
    mSuppMatrix.postTranslate(dx, dy)
    checkAndDisplayMatrix()

    /**
     * Here we decide whether to let the ImageView's parent to start taking
     * over the touch event.

     * First we check whether this function is enabled. We never want the
     * parent to take over if we're scaling. We then check the edge we're
     * on, and the direction of the scroll (i.e. if we're pulling against
     * the edge, aka 'overscrolling', let the parent take over).
     */
    val parent = imageView!!.parent
    if (mAllowParentInterceptOnEdge && !mScaleDragDetector!!.isScaling()) {
      if (mScrollEdge == EDGE_BOTH
          || mScrollEdge == EDGE_LEFT && dx >= 1f
          || mScrollEdge == EDGE_RIGHT && dx <= -1f) {
        parent?.requestDisallowInterceptTouchEvent(false)
      }
    } else {
      parent?.requestDisallowInterceptTouchEvent(true)
    }
  }

  override fun onFling(startX: Float, startY: Float, velocityX: Float,
                       velocityY: Float) {
    if (DEBUG) {
      LogManager.logger.d(
          LOG_TAG,
          "onFling. sX: " + startX + " sY: " + startY + " Vx: "
              + velocityX + " Vy: " + velocityY)
    }
    val imageView = imageView
    mCurrentFlingRunnable = FlingRunnable(imageView!!.context)
    mCurrentFlingRunnable!!.fling(getImageViewWidth(imageView),
        getImageViewHeight(imageView), velocityX.toInt(), velocityY.toInt())
    imageView.post(mCurrentFlingRunnable)
  }

  override fun onGlobalLayout() {
    val imageView = imageView

    if (null != imageView) {
      if (mZoomEnabled) {
        val top = imageView.top
        val right = imageView.right
        val bottom = imageView.bottom
        val left = imageView.left

        /**
         * We need to check whether the ImageView's bounds have changed.
         * This would be easier if we targeted API 11+ as we could just use
         * View.OnLayoutChangeListener. Instead we have to replicate the
         * work, keeping track of the ImageView's bounds and then checking
         * if the values change.
         */
        if (top != mIvTop || bottom != mIvBottom || left != mIvLeft
            || right != mIvRight) {
          // Update our base matrix, as the bounds have changed
          updateBaseMatrix(imageView.drawable)

          // Update values as something has changed
          mIvTop = top
          mIvRight = right
          mIvBottom = bottom
          mIvLeft = left
        }
      } else {
        updateBaseMatrix(imageView.drawable)
      }
    }
  }

  override fun onScale(scaleFactor: Float, focusX: Float, focusY: Float) {
    if (DEBUG) {
      LogManager.logger.d(
          LOG_TAG,
          String.format("onScale: scale: %.2f. fX: %.2f. fY: %.2f",
              scaleFactor, focusX, focusY))
    }

    if (getScale() < mMaxScale || scaleFactor < 1f) {
      mSuppMatrix.postScale(scaleFactor, scaleFactor, focusX, focusY)
      checkAndDisplayMatrix()
    }
  }

  override fun onTouch(v: View, ev: MotionEvent): Boolean {
    var handled = false

    if (mZoomEnabled && hasDrawable(v as ImageView)) {
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
          if (getScale() < mMinScale) {
            val rect = getDisplayRect()
            if (null != rect) {
              v.post(AnimatedZoomRunnable(getScale(), mMinScale,
                  rect.centerX(), rect.centerY()))
              handled = true
            }
          }
      }

      // Try the Scale/Drag detector
      if (null != mScaleDragDetector && mScaleDragDetector!!.onTouchEvent(ev)) {
        handled = true
      }

      // Check to see if the user double tapped
      if (null != mGestureDetector && mGestureDetector!!.onTouchEvent(ev)) {
        handled = true
      }
    }

    return handled
  }

  override fun setAllowParentInterceptOnEdge(allow: Boolean) {
    mAllowParentInterceptOnEdge = allow
  }

  @Deprecated("")
  override fun setMinScale(minScale: Float) {
    setMinimumScale(minScale)
  }

  override fun setMinimumScale(minimumScale: Float) {
    checkZoomLevels(minimumScale, mMidScale, mMaxScale)
    mMinScale = minimumScale
  }

  @Deprecated("")
  override fun setMidScale(midScale: Float) {
    setMediumScale(midScale)
  }

  override fun setMediumScale(mediumScale: Float) {
    checkZoomLevels(mMinScale, mediumScale, mMaxScale)
    mMidScale = mediumScale
  }

  @Deprecated("")
  override fun setMaxScale(maxScale: Float) {
    setMaximumScale(maxScale)
  }

  override fun setMaximumScale(maximumScale: Float) {
    checkZoomLevels(mMinScale, mMidScale, maximumScale)
    mMaxScale = maximumScale
  }

  override fun setOnLongClickListener(listener: OnLongClickListener) {
    mLongClickListener = listener
  }

  override fun setOnMatrixChangeListener(listener: OnMatrixChangedListener) {
    mMatrixChangeListener = listener
  }

  override fun setOnPhotoTapListener(listener: OnPhotoTapListener) {
    mPhotoTapListener = listener
  }

  override fun getOnPhotoTapListener(): OnPhotoTapListener? {
    return mPhotoTapListener
  }

  override fun setScale(scale: Float) {
    setScale(scale, false)
  }

  override fun setScale(scale: Float, animate: Boolean) {
    val imageView = imageView

    if (null != imageView) {
      setScale(scale,
          (imageView.right / 2).toFloat(),
          (imageView.bottom / 2).toFloat(),
          animate)
    }
  }

  override fun setScale(scale: Float, focalX: Float, focalY: Float,
                        animate: Boolean) {
    val imageView = imageView

    if (null != imageView) {
      // Check to see if the scale is within bounds
      if (scale < mMinScale || scale > mMaxScale) {
        LogManager
            .logger
            .i(LOG_TAG,
                "Scale must be within the range of minScale and maxScale")
        return
      }

      if (animate) {
        imageView.post(AnimatedZoomRunnable(getScale(), scale,
            focalX, focalY))
      } else {
        mSuppMatrix.setScale(scale, scale, focalX, focalY)
        checkAndDisplayMatrix()
      }
    }
  }

  override fun setScaleType(scaleType: ScaleType) {
    if (isSupportedScaleType(scaleType) && scaleType != mScaleType) {
      mScaleType = scaleType

      // Finally update
      update()
    }
  }

  override fun setZoomable(zoomable: Boolean) {
    mZoomEnabled = zoomable
    update()
  }

  fun update() {
    val imageView = imageView

    if (null != imageView) {
      if (mZoomEnabled) {
        // Make sure we using MATRIX Scale Type
        setImageViewScaleTypeMatrix(imageView)

        // Update the base matrix using the current drawable
        updateBaseMatrix(imageView.drawable)
      } else {
        // Reset the Matrix...
        resetMatrix()
      }
    }
  }

  val drawMatrix: Matrix
    get() {
      mDrawMatrix.set(mBaseMatrix)
      mDrawMatrix.postConcat(mSuppMatrix)
      return mDrawMatrix
    }

  private fun cancelFling() {
    if (null != mCurrentFlingRunnable) {
      mCurrentFlingRunnable!!.cancelFling()
      mCurrentFlingRunnable = null
    }
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
    val imageView = imageView

    /**
     * PhotoView's getScaleType() will just divert to this.getScaleType() so
     * only call if we're not attached to a PhotoView.
     */
    if (null != imageView && imageView !is IPhotoView) {
      if (ScaleType.MATRIX != imageView.scaleType) {
        throw IllegalStateException(
            "The ImageView's ScaleType has been changed since attaching a PhotoViewAttacher")
      }
    }
  }

  private fun checkMatrixBounds(): Boolean {
    val imageView = imageView ?: return false

    val rect = getDisplayRect(drawMatrix) ?: return false

    val height = rect.height()
    val width = rect.width()
    var deltaX = 0f
    var deltaY = 0f

    val viewHeight = getImageViewHeight(imageView)
    if (height <= viewHeight) {
      when (mScaleType) {
        ImageView.ScaleType.FIT_START -> deltaY = -rect.top
        ImageView.ScaleType.FIT_END -> deltaY = viewHeight.toFloat() - height - rect.top
        else -> deltaY = (viewHeight - height) / 2 - rect.top
      }
    } else if (rect.top > 0) {
      deltaY = -rect.top
    } else if (rect.bottom < viewHeight) {
      deltaY = viewHeight - rect.bottom
    }

    val viewWidth = getImageViewWidth(imageView)
    if (width <= viewWidth) {
      when (mScaleType) {
        ImageView.ScaleType.FIT_START -> deltaX = -rect.left
        ImageView.ScaleType.FIT_END -> deltaX = viewWidth.toFloat() - width - rect.left
        else -> deltaX = (viewWidth - width) / 2 - rect.left
      }
      mScrollEdge = EDGE_BOTH
    } else if (rect.left > 0) {
      mScrollEdge = EDGE_LEFT
      deltaX = -rect.left
    } else if (rect.right < viewWidth) {
      deltaX = viewWidth - rect.right
      mScrollEdge = EDGE_RIGHT
    } else {
      mScrollEdge = EDGE_NONE
    }

    // Finally actually translate the matrix
    mSuppMatrix.postTranslate(deltaX, deltaY)
    return true
  }

  /**
   * Helper method that maps the supplied Matrix to the current Drawable

   * @param matrix - Matrix to map Drawable against
   * *
   * @return RectF - Displayed Rectangle
   */
  private fun getDisplayRect(matrix: Matrix): RectF? {
    val imageView = imageView

    if (null != imageView) {
      val d = imageView.drawable
      if (null != d) {
        mDisplayRect.set(0f, 0f, d.intrinsicWidth.toFloat(),
            d.intrinsicHeight.toFloat())
        matrix.mapRect(mDisplayRect)
        return mDisplayRect
      }
    }
    return null
  }

  override fun getVisibleRectangleBitmap(): Bitmap? {
    val imageView = imageView
    return imageView?.drawingCache
  }

  override fun setZoomTransitionDuration(duration: Int) {
    var milliseconds = duration
    if (milliseconds < 0)
      milliseconds = IPhotoView.DEFAULT_ZOOM_DURATION
    this.ZOOM_DURATION = milliseconds
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
    matrix.getValues(mMatrixValues)
    return mMatrixValues[whichValue]
  }

  /**
   * Resets the Matrix back to FIT_CENTER, and then displays it.s
   */
  private fun resetMatrix() {
    mSuppMatrix.reset()
    setImageViewMatrix(drawMatrix)
    checkMatrixBounds()
  }

  private fun setImageViewMatrix(matrix: Matrix) {
    val imageView = imageView
    if (null != imageView) {

      checkImageViewScaleType()
      imageView.imageMatrix = matrix

      // Call MatrixChangedListener if needed
      if (null != mMatrixChangeListener) {
        val displayRect = getDisplayRect(matrix)
        if (null != displayRect) {
          mMatrixChangeListener!!.onMatrixChanged(displayRect)
        }
      }
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

    val viewWidth = getImageViewWidth(imageView).toFloat()
    val viewHeight = getImageViewHeight(imageView).toFloat()
    val drawableWidth = d.intrinsicWidth
    val drawableHeight = d.intrinsicHeight

    mBaseMatrix.reset()

    val widthScale = viewWidth / drawableWidth
    val heightScale = viewHeight / drawableHeight

    if (mScaleType == ScaleType.CENTER) {
      mBaseMatrix.postTranslate((viewWidth - drawableWidth) / 2f,
          (viewHeight - drawableHeight) / 2f)

    } else if (mScaleType == ScaleType.CENTER_CROP) {
      val scale = Math.max(widthScale, heightScale)
      mBaseMatrix.postScale(scale, scale)
      mBaseMatrix.postTranslate((viewWidth - drawableWidth * scale) / 2f,
          (viewHeight - drawableHeight * scale) / 2f)

    } else if (mScaleType == ScaleType.CENTER_INSIDE) {
      val scale = Math.min(1.0f, Math.min(widthScale, heightScale))
      mBaseMatrix.postScale(scale, scale)
      mBaseMatrix.postTranslate((viewWidth - drawableWidth * scale) / 2f,
          (viewHeight - drawableHeight * scale) / 2f)

    } else {
      val mTempSrc = RectF(0f, 0f, drawableWidth.toFloat(), drawableHeight.toFloat())
      val mTempDst = RectF(0f, 0f, viewWidth, viewHeight)

      when (mScaleType) {
        ImageView.ScaleType.FIT_CENTER -> mBaseMatrix
            .setRectToRect(mTempSrc, mTempDst, ScaleToFit.CENTER)

        ImageView.ScaleType.FIT_START -> mBaseMatrix.setRectToRect(mTempSrc, mTempDst, ScaleToFit.START)

        ImageView.ScaleType.FIT_END -> mBaseMatrix.setRectToRect(mTempSrc, mTempDst, ScaleToFit.END)

        ImageView.ScaleType.FIT_XY -> mBaseMatrix.setRectToRect(mTempSrc, mTempDst, ScaleToFit.FILL)

        else -> {
        }
      }
    }

    resetMatrix()
  }

  private fun getImageViewWidth(imageView: ImageView?): Int {
    if (null == imageView)
      return 0
    return imageView.width - imageView.paddingLeft - imageView.paddingRight
  }

  private fun getImageViewHeight(imageView: ImageView?): Int {
    if (null == imageView)
      return 0
    return imageView.height - imageView.paddingTop - imageView.paddingBottom
  }

  /**
   * Interface definition for a callback to be invoked when the internal Matrix has changed for
   * this View.

   * @author Chris Banes
   */
  interface OnMatrixChangedListener {
    /**
     * Callback for when the Matrix displaying the Drawable has changed. This could be because
     * the View's bounds have changed, or the user has zoomed.

     * @param rect - Rectangle displaying the Drawable's new bounds.
     */
    fun onMatrixChanged(rect: RectF)
  }

  /**
   * Interface definition for a callback to be invoked when the Photo is tapped with a single
   * tap.

   * @author Chris Banes
   */
  interface OnPhotoTapListener {

    /**
     * A callback to receive where the user taps on a photo. You will only receive a callback if
     * the user taps on the actual photo, tapping on 'whitespace' will be ignored.

     * @param view - View the user tapped.
     * *
     * @param x    - where the user tapped from the of the Drawable, as percentage of the
     * *             Drawable width.
     * *
     * @param y    - where the user tapped from the top of the Drawable, as percentage of the
     * *             Drawable height.
     */
    fun onPhotoTap(view: View, x: Float, y: Float)
  }

  /**
   * Interface definition for a callback to be invoked when the ImageView is tapped with a single
   * tap.

   * @author Chris Banes
   */
  interface OnViewTapListener {

    /**
     * A callback to receive where the user taps on a ImageView. You will receive a callback if
     * the user taps anywhere on the view, tapping on 'whitespace' will not be ignored.

     * @param view - View the user tapped.
     * *
     * @param x    - where the user tapped from the left of the View.
     * *
     * @param y    - where the user tapped from the top of the View.
     */
    fun onViewTap(view: View, x: Float, y: Float)
  }

  private inner class AnimatedZoomRunnable(private val mZoomStart: Float, private val mZoomEnd: Float,
                                           private val mFocalX: Float, private val mFocalY: Float) : Runnable {
    private val mStartTime: Long

    init {
      mStartTime = System.currentTimeMillis()
    }

    override fun run() {
      val imageView = imageView ?: return

      val t = interpolate()
      val scale = mZoomStart + t * (mZoomEnd - mZoomStart)
      val deltaScale = scale / getScale()

      mSuppMatrix.postScale(deltaScale, deltaScale, mFocalX, mFocalY)
      checkAndDisplayMatrix()

      // We haven't hit our target scale yet, so post ourselves again
      if (t < 1f) {
        Compat.postOnAnimation(imageView, this)
      }
    }

    private fun interpolate(): Float {
      var t = 1f * (System.currentTimeMillis() - mStartTime) / ZOOM_DURATION
      t = Math.min(1f, t)
      t = sInterpolator.getInterpolation(t)
      return t
    }
  }

  private inner class FlingRunnable(context: Context) : Runnable {

    private val mScroller: ScrollerProxy
    private var mCurrentX: Int = 0
    private var mCurrentY: Int = 0

    init {
      mScroller = ScrollerProxy.getScroller(context)
    }

    fun cancelFling() {
      if (DEBUG) {
        LogManager.logger.d(LOG_TAG, "Cancel Fling")
      }
      mScroller.forceFinished(true)
    }

    fun fling(viewWidth: Int, viewHeight: Int, velocityX: Int,
              velocityY: Int) {
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

      mCurrentX = startX
      mCurrentY = startY

      if (DEBUG) {
        LogManager.logger.d(
            LOG_TAG,
            "fling. StartX:" + startX + " StartY:" + startY
                + " MaxX:" + maxX + " MaxY:" + maxY)
      }

      // If we actually can move, fling the scroller
      if (startX != maxX || startY != maxY) {
        mScroller.fling(startX, startY, velocityX, velocityY, minX,
            maxX, minY, maxY, 0, 0)
      }
    }

    override fun run() {
      if (mScroller.isFinished) {
        return  // remaining post that should not be handled
      }

      val imageView = imageView
      if (null != imageView && mScroller.computeScrollOffset()) {

        val newX = mScroller.currX
        val newY = mScroller.currY

        if (DEBUG) {
          LogManager.logger.d(
              LOG_TAG,
              "fling run(). CurrentX:" + mCurrentX + " CurrentY:"
                  + mCurrentY + " NewX:" + newX + " NewY:"
                  + newY)
        }

        mSuppMatrix.postTranslate((mCurrentX - newX).toFloat(), (mCurrentY - newY).toFloat())
        setImageViewMatrix(drawMatrix)

        mCurrentX = newX
        mCurrentY = newY

        // Post On animation
        Compat.postOnAnimation(imageView, this)
      }
    }
  }

  companion object {

    private val LOG_TAG = "PhotoViewAttacher"

    // let debug flag be dynamic, but still Proguard can be used to remove from
    // release builds
    private val DEBUG = Log.isLoggable(LOG_TAG, Log.DEBUG)

    internal val sInterpolator: Interpolator = AccelerateDecelerateInterpolator()

    internal val EDGE_NONE = -1
    internal val EDGE_LEFT = 0
    internal val EDGE_RIGHT = 1
    internal val EDGE_BOTH = 2

    private fun checkZoomLevels(minZoom: Float, midZoom: Float,
                                maxZoom: Float) {
      if (minZoom >= midZoom) {
        throw IllegalArgumentException(
            "MinZoom has to be less than MidZoom")
      } else if (midZoom >= maxZoom) {
        throw IllegalArgumentException(
            "MidZoom has to be less than MaxZoom")
      }
    }

    /**
     * @return true if the ImageView exists, and it's Drawable existss
     */
    private fun hasDrawable(imageView: ImageView?): Boolean {
      return null != imageView && null != imageView.drawable
    }

    /**
     * @return true if the ScaleType is supported.
     */
    private fun isSupportedScaleType(scaleType: ScaleType?): Boolean {
      if (null == scaleType) {
        return false
      }

      when (scaleType) {
        ImageView.ScaleType.MATRIX -> throw IllegalArgumentException(scaleType.name + " is not supported in PhotoView")

        else -> return true
      }
    }

    /**
     * Set's the ImageView's ScaleType to Matrix.
     */
    private fun setImageViewScaleTypeMatrix(imageView: ImageView?) {
      /**
       * PhotoView sets it's own ScaleType to Matrix, then diverts all calls
       * setScaleType to this.setScaleType automatically.
       */
      if (null != imageView && imageView !is IPhotoView) {
        if (ScaleType.MATRIX != imageView.scaleType) {
          imageView.scaleType = ScaleType.MATRIX
        }
      }
    }
  }
}
