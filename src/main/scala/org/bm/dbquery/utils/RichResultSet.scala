/*
 * Copyright (c) 2015. Baptiste MORIN.
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

import java.sql.ResultSet

import scala.annotation.tailrec
import scala.language.implicitConversions

/**
  *
  * @author morinb.
  */
class RichResultSet(rs: ResultSet) {

  def map[T](f: ResultSet => T): List[T] = {
    @tailrec
    def accu(acc: List[T]): List[T] = {
      if (rs.next()) {
        val t: T = f(rs)
        accu(t :: acc)
      } else {
        acc
      }
    }
    accu(Nil).reverse
  }

}

object RichResultSet {
  implicit def enrichResultSet(rs: ResultSet): RichResultSet = new RichResultSet(rs)
}


