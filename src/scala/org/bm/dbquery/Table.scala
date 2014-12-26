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
class Table(val catalog: Option[String],
            val schema: Option[String],
            val name: String,
            val tableType: String /*TABLE, VIEW, SYSTEM TABLE, GLOBAL TEMPORARY, LOCAL TEMPORARY, ALIAS, SYNONYM*/ ,
            val remarks: Option[String]) {

  override def toString: String = s"$name"
}

object Table {

  def apply(tableNamePattern: String = null, catalogPattern: String = null, schemaPattern: String = null)(implicit conn: Connection): List[Table] =
    Table(conn.getMetaData.getTables(catalogPattern, schemaPattern, tableNamePattern, null))

  def apply(rs: ResultSet): List[Table] = {

    @tailrec
    def accumulator(acc: List[Table]): List[Table] = {
      if (rs.next()) {
        val tab = new Table(
          Option(rs.getString("TABLE_CAT")),
          Option(rs.getString("TABLE_SCHEM")),
          rs.getString("TABLE_NAME"),
          rs.getString("TABLE_TYPE"),
          Option(rs.getString("REMARKS")))

        accumulator(acc :+ tab)
      } else {
        acc
      }
    }
    accumulator(Nil)
  }
}
