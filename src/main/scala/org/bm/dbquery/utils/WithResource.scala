/*
 * Copyright (c) 2014. Baptiste MORIN
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.bm.dbquery.utils

import scala.language.reflectiveCalls

/**
 *
 * @author Baptiste Morin
 */
object WithResource {
  def withResource[ReturnType](closeable: {def close(): Unit})(todo: => ReturnType): ReturnType = {
    var throwable: Throwable = null

    try {
      todo
    } catch {
      case exceptionTodo: Exception => throwable = exceptionTodo
        throw exceptionTodo
    } finally {
      if (closeable != null) {
        if (throwable != null) {
          try {
            closeable.close()
          } catch {
            case exceptionClose: Exception => throwable.addSuppressed(exceptionClose)
          }
        } else {
          closeable.close()
        }
      }
    }
  }
}
