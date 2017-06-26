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

import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.RectF
import android.view.GestureDetector
import android.view.View
import android.widget.ImageView


interface IPhotoView {

  /**
   * Gets the Display Rectangle of the currently displayed Drawable. The Rectangle is relative to
   * this View and includes all scaling and translations.

   * @return - RectF of Displayed Drawable
   */
  fun getDisplayRect(): RectF?


  /**
   * Sets the Display Matrix of the currently displayed Drawable. The Rectangle is considered
   * relative to this View and includes all scaling and translations.

   * @param finalMatrix target matrix to set PhotoView to
   * *
   * @return - true if rectangle was applied successfully
   */
  fun setDisplayMatrix(finalMatrix: Matrix): Boolean

  /**
   * @return The current minimum scale level. What this value represents depends on the current
   * * [android.widget.ImageView.ScaleType].
   */
  fun getMinimumScale(): Float

  /**
   * @return The current medium scale level. What this value represents depends on the current
   * * [android.widget.ImageView.ScaleType].
   */
  fun getMediumScale(): Float

  /**
   * @return The current maximum scale level. What this value represents depends on the current
   * * [android.widget.ImageView.ScaleType].
   */
  fun getMaximumScale(): Float

  /**
   * Returns the current scale value

   * @return float - current scale value
   */
  fun getScale(): Float

  /**
   * Changes the current scale to the specified value, around the given focal point.

   * @param scale   - Value to scale to
   * *
   * @param focalX  - X Focus Point
   * *
   * @param focalY  - Y Focus Point
   */
  fun setScale(scale: Float, focalX: Float, focalY: Float)

  companion object {

    val DEFAULT_MAX_SCALE = 3.0f
    val DEFAULT_MID_SCALE = 1.75f
    val DEFAULT_MIN_SCALE = 1.0f
    val DEFAULT_ZOOM_DURATION = 200
  }
}
