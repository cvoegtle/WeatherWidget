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

/**
 * interface for a logger class to replace the static calls to [android.util.Log]
 */
interface Logger {
  /**
   * Send a [android.util.Log.DEBUG] log message.

   * @param tag Used to identify the source of a log message.  It usually identifies
   * *            the class or activity where the log call occurs.
   * *
   * @param msg The message you would like logged.
   */
  fun d(tag: String, msg: String): Int

  /**
   * Send an [android.util.Log.INFO] log message.

   * @param tag Used to identify the source of a log message.  It usually identifies
   * *            the class or activity where the log call occurs.
   * *
   * @param msg The message you would like logged.
   */
  fun i(tag: String, msg: String): Int

  /**
   * Send an [android.util.Log.ERROR] log message.

   * @param tag Used to identify the source of a log message.  It usually identifies
   * *            the class or activity where the log call occurs.
   * *
   * @param msg The message you would like logged.
   */
  fun e(tag: String, msg: String): Int

  /**
   * Send a [android.util.Log.ERROR] log message and log the exception.

   * @param tag Used to identify the source of a log message.  It usually identifies
   * *            the class or activity where the log call occurs.
   * *
   * @param msg The message you would like logged.
   * *
   * @param tr  An exception to log
   */
  fun e(tag: String, msg: String, tr: Throwable): Int
}
