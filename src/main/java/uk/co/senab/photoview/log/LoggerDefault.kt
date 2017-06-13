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
package uk.co.senab.photoview.log

import android.util.Log

/**
 * Helper class to redirect [LogManager.logger] to [Log]
 */
class LoggerDefault : Logger {

  override fun d(tag: String, msg: String): Int {
    return Log.d(tag, msg)
  }

  override fun i(tag: String, msg: String): Int {
    return Log.i(tag, msg)
  }

  override fun e(tag: String, msg: String): Int {
    return Log.e(tag, msg)
  }

  override fun e(tag: String, msg: String, tr: Throwable): Int {
    return Log.e(tag, msg, tr)
  }


}
