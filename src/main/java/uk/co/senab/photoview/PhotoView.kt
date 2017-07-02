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
import android.graphics.Matrix
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.AttributeSet
import android.widget.ImageView

class PhotoView @JvmOverloads constructor(context: Context, attr: AttributeSet? = null, defStyle: Int = 0) : ImageView(context, attr, defStyle), IPhotoView {

  private val mAttacher: PhotoViewAttacher

  init {
    super.setScaleType(ImageView.ScaleType.MATRIX)
    mAttacher = PhotoViewAttacher(this)
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

  override fun setScale(scale: Float, focalX: Float, focalY: Float) {
    mAttacher.setScale(scale, focalX, focalY)
  }

  override fun onDetachedFromWindow() {
    mAttacher.cleanup()
    super.onDetachedFromWindow()
  }

}