/*
 * Copyright (c) 2014. Baptiste MORIN (408658)
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

package org.bm.dbquery

import java.sql.{Connection, ResultSet}

import scala.annotation.tailrec

/**
 *
 * @author Baptiste Morin
 */
class Catalog(val name: String) {
  override def toString: String = name
}

object Catalog {
  def apply(conn: Connection): List[Catalog] = Catalog(conn.getMetaData.getCatalogs)

  def apply(rs: ResultSet): List[Catalog] = {
    @tailrec
    def accumulator(acc: List[Catalog]): List[Catalog] =
      if (rs.next()) {
        val c = new Catalog(rs.getString("TABLE_CAT"))
        accumulator(acc :+ c)
      } else acc

    accumulator(Nil)

  }
}
