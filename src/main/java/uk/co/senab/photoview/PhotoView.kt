/*******************************************************************************
 * Copyright 2011, 2012 Chris Banes.

 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at

 * http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.co.senab.photoview

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.View
import android.widget.ImageView

import uk.co.senab.photoview.PhotoViewAttacher.OnMatrixChangedListener
import uk.co.senab.photoview.PhotoViewAttacher.OnPhotoTapListener
import uk.co.senab.photoview.PhotoViewAttacher.OnViewTapListener

class PhotoView @JvmOverloads constructor(context: Context, attr: AttributeSet? = null, defStyle: Int = 0) : ImageView(context, attr, defStyle), IPhotoView {

  private val mAttacher: PhotoViewAttacher?

  private var mPendingScaleType: ImageView.ScaleType? = null

  init {
    super.setScaleType(ImageView.ScaleType.MATRIX)
    mAttacher = PhotoViewAttacher(this)

    if (null != mPendingScaleType) {
      scaleType = mPendingScaleType!!
      mPendingScaleType = null
    }
  }


  @Deprecated("use {@link #setRotationTo(float)}")
  override fun setPhotoViewRotation(rotationDegree: Float) {
    mAttacher!!.setRotationTo(rotationDegree)
  }

  override fun setRotationTo(rotationDegree: Float) {
    mAttacher!!.setRotationTo(rotationDegree)
  }

  override fun setRotationBy(rotationDegree: Float) {
    mAttacher!!.setRotationBy(rotationDegree)
  }

  override fun canZoom(): Boolean {
    return mAttacher!!.canZoom()
  }

  override fun getDisplayRect(): RectF? {
    return mAttacher!!.displayRect
  }

  override fun setDisplayMatrix(finalRectangle: Matrix): Boolean {
    return mAttacher!!.setDisplayMatrix(finalRectangle)
  }

  @Deprecated("")
  override fun getMinScale(): Float {
    return minimumScale
  }

  override fun getMinimumScale(): Float {
    return mAttacher!!.minimumScale
  }

  @Deprecated("")
  override fun getMidScale(): Float {
    return mediumScale
  }

  override fun getMediumScale(): Float {
    return mAttacher!!.mediumScale
  }

  @Deprecated("")
  override fun getMaxScale(): Float {
    return maximumScale
  }

  override fun getMaximumScale(): Float {
    return mAttacher!!.maximumScale
  }

  override fun getScale(): Float {
    return mAttacher!!.scale
  }

  override fun getScaleType(): ImageView.ScaleType {
    return mAttacher!!.scaleType
  }

  override fun setAllowParentInterceptOnEdge(allow: Boolean) {
    mAttacher!!.setAllowParentInterceptOnEdge(allow)
  }

  @Deprecated("")
  override fun setMinScale(minScale: Float) {
    minimumScale = minScale
  }

  override fun setMinimumScale(minimumScale: Float) {
    mAttacher!!.minimumScale = minimumScale
  }

  @Deprecated("")
  override fun setMidScale(midScale: Float) {
    mediumScale = midScale
  }

  override fun setMediumScale(mediumScale: Float) {
    mAttacher!!.mediumScale = mediumScale
  }

  @Deprecated("")
  override fun setMaxScale(maxScale: Float) {
    maximumScale = maxScale
  }

  override fun setMaximumScale(maximumScale: Float) {
    mAttacher!!.maximumScale = maximumScale
  }

  override // setImageBitmap calls through to this method
  fun setImageDrawable(drawable: Drawable) {
    super.setImageDrawable(drawable)
    mAttacher?.update()
  }

  override fun setImageResource(resId: Int) {
    super.setImageResource(resId)
    mAttacher?.update()
  }

  override fun setImageURI(uri: Uri) {
    super.setImageURI(uri)
    mAttacher?.update()
  }

  override fun setOnMatrixChangeListener(listener: OnMatrixChangedListener) {
    mAttacher!!.setOnMatrixChangeListener(listener)
  }

  override fun setOnLongClickListener(l: View.OnLongClickListener) {
    mAttacher!!.setOnLongClickListener(l)
  }

  override fun setOnPhotoTapListener(listener: OnPhotoTapListener) {
    mAttacher!!.setOnPhotoTapListener(listener)
  }

  override fun getOnPhotoTapListener(): OnPhotoTapListener? {
    return mAttacher!!.onPhotoTapListener
  }

  override fun setOnViewTapListener(listener: OnViewTapListener) {
    mAttacher!!.setOnViewTapListener(listener)
  }

  override fun getOnViewTapListener(): OnViewTapListener? {
    return mAttacher!!.onViewTapListener
  }

  override fun setScale(scale: Float) {
    mAttacher!!.scale = scale
  }

  override fun setScale(scale: Float, animate: Boolean) {
    mAttacher!!.setScale(scale, animate)
  }

  override fun setScale(scale: Float, focalX: Float, focalY: Float, animate: Boolean) {
    mAttacher!!.setScale(scale, focalX, focalY, animate)
  }

  override fun setScaleType(scaleType: ImageView.ScaleType) {
    if (null != mAttacher) {
      mAttacher.scaleType = scaleType
    } else {
      mPendingScaleType = scaleType
    }
  }

  override fun setZoomable(zoomable: Boolean) {
    mAttacher!!.setZoomable(zoomable)
  }

  override fun getVisibleRectangleBitmap(): Bitmap? {
    return mAttacher!!.visibleRectangleBitmap
  }

  override fun setZoomTransitionDuration(duration: Int) {
    mAttacher!!.setZoomTransitionDuration(duration)
  }

  override fun setOnDoubleTapListener(newOnDoubleTapListener: GestureDetector.OnDoubleTapListener) {
    mAttacher!!.setOnDoubleTapListener(newOnDoubleTapListener)
  }

  override fun onDetachedFromWindow() {
    mAttacher!!.cleanup()
    super.onDetachedFromWindow()
  }

}