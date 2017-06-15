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

class PhotoView @JvmOverloads constructor(context: Context, attr: AttributeSet? = null, defStyle: Int = 0) : ImageView(context, attr, defStyle), IPhotoView {

  private val mAttacher: PhotoViewAttacher

  init {
    super.setScaleType(ImageView.ScaleType.MATRIX)
    mAttacher = PhotoViewAttacher(this)
  }


  override fun setRotationTo(rotationDegree: Float) {
    mAttacher.setRotationTo(rotationDegree)
  }

  override fun setRotationBy(rotationDegree: Float) {
    mAttacher.setRotationBy(rotationDegree)
  }

  override fun canZoom(): Boolean {
    return mAttacher.canZoom()
  }

  override fun getDisplayRect(): RectF? {
    return mAttacher.getDisplayRect()
  }

  override fun setDisplayMatrix(finalMatrix: Matrix): Boolean {
    return mAttacher.setDisplayMatrix(finalMatrix)
  }

  override fun getMinimumScale(): Float {
    return mAttacher.getMinimumScale()
  }

  override fun getMediumScale(): Float {
    return mAttacher.getMediumScale()
  }

  override fun getMaximumScale(): Float {
    return mAttacher.getMaximumScale()
  }

  override fun getScale(): Float {
    return mAttacher.getScale()
  }

  override fun getScaleType(): ImageView.ScaleType {
    return mAttacher.getScaleType()
  }

  override fun setAllowParentInterceptOnEdge(allow: Boolean) {
    mAttacher.setAllowParentInterceptOnEdge(allow)
  }

  override fun setMinimumScale(scale: Float) {
    mAttacher.setMinimumScale(scale)
  }

  override fun setMediumScale(scale: Float) {
    mAttacher.setMediumScale(scale)
  }

  override fun setMaximumScale(scale: Float) {
    mAttacher.setMaximumScale(scale)
  }

  override // setImageBitmap calls through to this method
  fun setImageDrawable(drawable: Drawable) {
    super.setImageDrawable(drawable)
    mAttacher.update()
  }

  override fun setImageResource(resId: Int) {
    super.setImageResource(resId)
    mAttacher.update()
  }

  override fun setImageURI(uri: Uri) {
    super.setImageURI(uri)
    mAttacher.update()
  }

  override fun setOnMatrixChangeListener(listener: OnMatrixChangedListener) {
    mAttacher.setOnMatrixChangeListener(listener)
  }

  override fun setOnLongClickListener(l: View.OnLongClickListener) {
    mAttacher.setOnLongClickListener(l)
  }

  override fun setScale(scale: Float) {
    mAttacher.setScale(scale)
  }

  override fun setScale(scale: Float, animate: Boolean) {
    mAttacher.setScale(scale, animate)
  }

  override fun setScale(scale: Float, focalX: Float, focalY: Float, animate: Boolean) {
    mAttacher.setScale(scale, focalX, focalY, animate)
  }

  override fun setScaleType(scaleType: ImageView.ScaleType) {
      mAttacher.setScaleType(scaleType)
  }

  override fun setZoomable(zoomable: Boolean) {
    mAttacher.setZoomable(zoomable)
  }

  override fun getVisibleRectangleBitmap(): Bitmap? {
    return mAttacher.getVisibleRectangleBitmap()
  }

  override fun onDetachedFromWindow() {
    mAttacher.cleanup()
    super.onDetachedFromWindow()
  }

}