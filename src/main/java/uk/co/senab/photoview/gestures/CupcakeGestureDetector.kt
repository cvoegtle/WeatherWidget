/*******************************************************************************
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
 */
package uk.co.senab.photoview.gestures

import android.content.Context
import android.util.Log
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.ViewConfiguration

open class CupcakeGestureDetector(context: Context, val mListener: OnGestureListener) : GestureDetector {
  private val LOG_TAG = "CupcakeGestureDetector"

  internal var mLastTouchX: Float = 0.toFloat()
  internal var mLastTouchY: Float = 0.toFloat()
  internal val mTouchSlop: Float
  internal val mMinimumVelocity: Float

  init {
    val configuration = ViewConfiguration.get(context)
    mMinimumVelocity = configuration.scaledMinimumFlingVelocity.toFloat()
    mTouchSlop = configuration.scaledTouchSlop.toFloat()
  }

  private var mVelocityTracker: VelocityTracker? = null
  private var mIsDragging: Boolean = false

  open internal fun getActiveX(ev: MotionEvent): Float {
    return ev.x
  }

  open internal fun getActiveY(ev: MotionEvent): Float {
    return ev.y
  }

  override fun isScaling(): Boolean {
    return false
  }

  override fun onTouchEvent(ev: MotionEvent): Boolean {
    when (ev.action) {
      MotionEvent.ACTION_DOWN -> {
        mVelocityTracker = VelocityTracker.obtain()
        mVelocityTracker?.addMovement(ev) ?: Log.i(LOG_TAG, "Velocity tracker is null")

        mLastTouchX = getActiveX(ev)
        mLastTouchY = getActiveY(ev)
        mIsDragging = false
      }

      MotionEvent.ACTION_MOVE -> {
        val x = getActiveX(ev)
        val y = getActiveY(ev)
        val dx = x - mLastTouchX
        val dy = y - mLastTouchY

        if (!mIsDragging) {
          // Use Pythagoras to see if drag length is larger than
          // touch slop
          mIsDragging = Math.sqrt((dx * dx + dy * dy).toDouble()) >= mTouchSlop
        }

        if (mIsDragging) {
          mListener.onDrag(dx, dy)
          mLastTouchX = x
          mLastTouchY = y

          mVelocityTracker?.addMovement(ev)
        }
      }

      MotionEvent.ACTION_CANCEL -> {
        // Recycle Velocity Tracker
        mVelocityTracker?.recycle()
        mVelocityTracker = null
      }

      MotionEvent.ACTION_UP -> {
        if (mIsDragging) {
          if (null != mVelocityTracker) {
            mLastTouchX = getActiveX(ev)
            mLastTouchY = getActiveY(ev)

            // Compute velocity within the last 1000ms
            mVelocityTracker!!.addMovement(ev)
            mVelocityTracker!!.computeCurrentVelocity(1000)

            val vX = mVelocityTracker!!.xVelocity
            val vY = mVelocityTracker!!.yVelocity

            // If the velocity is greater than minVelocity, call
            // listener
            if (Math.max(Math.abs(vX), Math.abs(vY)) >= mMinimumVelocity) {
              mListener.onFling(mLastTouchX, mLastTouchY, -vX, -vY)
            }
          }
        }

        // Recycle Velocity Tracker
        mVelocityTracker?.recycle()
        mVelocityTracker = null
      }
    }

    return true
  }
}
