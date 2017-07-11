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
package uk.co.senab.photoview.gestures

import android.annotation.TargetApi
import android.content.Context
import android.util.Log
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.VelocityTracker
import android.view.ViewConfiguration
import uk.co.senab.photoview.Compat

@TargetApi(8)
class PhotoGestureDetector(context: Context, val listener: OnGestureListener) : IGestureDetector {
  private val LOG_TAG: String = "PhotoGestureDetector"

  private var activePointerId: Int? = null
  private var activePointerIndex = 0

  internal var lastTouch = Position(0.0F, 0.0F )
  internal val touchSlop: Float
  internal val minimumVelocity: Float

  init {
    val configuration = ViewConfiguration.get(context)
    minimumVelocity = configuration.scaledMinimumFlingVelocity.toFloat()
    touchSlop = configuration.scaledTouchSlop.toFloat()
  }

  private var velocityTracker: VelocityTracker? = null
  private var isDragging: Boolean = false

  fun getActivePosition(ev: MotionEvent): Position = getPosition(ev, activePointerIndex)

  fun getPosition(ev: MotionEvent, pointerIndex: Int): Position {
    try {
      return Position(x = ev.getX(pointerIndex), y = ev.getY(pointerIndex))
    } catch (ex: Exception) {
      Log.w(LOG_TAG, "Exception accessing pointer index $pointerIndex")
      return Position(x = ev.x, y = ev.y)
    }
  }

  override fun onTouchEvent(ev: MotionEvent): Boolean {
    mDetector.onTouchEvent(ev)

    val action = ev.action
    when (action and MotionEvent.ACTION_MASK) {
      MotionEvent.ACTION_POINTER_UP -> {
        // Ignore deprecation, ACTION_POINTER_ID_MASK and
        // ACTION_POINTER_ID_SHIFT has same value and are deprecated
        // You can have either deprecation or lint target api warning
        val pointerIndex = Compat.getPointerIndex(ev.action)
        val pointerId = ev.getPointerId(pointerIndex)
        if (pointerId == activePointerId) {
          // This was our active pointer going up. Choose a new
          // active pointer and adjust accordingly.
          val newPointerIndex = if (pointerIndex == 0) 1 else 0
          activePointerId = ev.getPointerId(newPointerIndex)
          lastTouch = getPosition(ev, newPointerIndex)
        }
      }
      MotionEvent.ACTION_DOWN -> {
        activePointerId = ev.getPointerId(0)
        velocityTracker = VelocityTracker.obtain()
        velocityTracker?.addMovement(ev) ?: Log.i(LOG_TAG, "Velocity tracker is null")

        lastTouch = getActivePosition(ev)
        isDragging = false
      }
      MotionEvent.ACTION_MOVE -> {
        val position = getActivePosition(ev)
        val dx = position.x - lastTouch.x
        val dy = position.y - lastTouch.y

        if (!isDragging) {
          // Use Pythagoras to see if drag length is larger than
          // touch slop
          isDragging = Math.sqrt((dx * dx + dy * dy).toDouble()) >= touchSlop
        }

        if (isDragging) {
          listener.onDrag(dx, dy)
          lastTouch = position

          velocityTracker?.addMovement(ev)
        }
      }

      MotionEvent.ACTION_CANCEL -> {
        // Recycle Velocity Tracker
        cleanUpVelocityTracker()
      }


      MotionEvent.ACTION_UP -> {
        if (isDragging) {
          velocityTracker?.let {
            lastTouch = getActivePosition(ev)

            // Compute velocity within the last 1000ms
            it.addMovement(ev)
            it.computeCurrentVelocity(1000)

            val vX = it.xVelocity
            val vY = it.yVelocity

            // If the velocity is greater than minVelocity, call
            // listener
            if (Math.max(Math.abs(vX), Math.abs(vY)) >= minimumVelocity) {
              listener.onFling(lastTouch.x, lastTouch.y, -vX, -vY)
            }
          }
        }

        // Recycle Velocity Tracker
        cleanUpVelocityTracker()
      }

    }
    activePointerIndex = ev.findPointerIndex(activePointerId ?: 0)
    return true
  }

  private fun cleanUpVelocityTracker() {
    velocityTracker?.recycle()
    velocityTracker = null
    activePointerId = null
  }


  private val mDetector = ScaleGestureDetector(context, object : ScaleGestureDetector.OnScaleGestureListener {

    override fun onScale(detector: ScaleGestureDetector): Boolean {
      val scaleFactor = detector.scaleFactor

      if (java.lang.Float.isNaN(scaleFactor) || java.lang.Float.isInfinite(scaleFactor)) {
        return false
      }

      listener.onScale(scaleFactor, detector.focusX, detector.focusY)
      return true
    }

    override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
      return true
    }

    override fun onScaleEnd(detector: ScaleGestureDetector) {
      // NO-OP
    }
  })


  override fun isScaling(): Boolean {
    return mDetector.isInProgress
  }

}
