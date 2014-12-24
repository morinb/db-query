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
class Schema(val catalog: Option[String], val name: String) {
  override def toString: String = s"${
    catalog match {
      case Some(c) => c + "."
      case None => ""
    }
  }$name"
}

object Schema {
  def apply(conn: Connection, catalog: Catalog): List[Schema] =
    Schema(conn.getMetaData.getSchemas(catalog.name, null))


  def apply(conn: Connection, catalog: String, schemaNamePattern: String = null): List[Schema] =
    Schema(conn.getMetaData.getSchemas(catalog, schemaNamePattern))

  def apply(rs: ResultSet): List[Schema] = {
    @tailrec
    def accumulator(acc: List[Schema]): List[Schema] =
      if (rs.next()) {
        val s = new Schema(
          Option(rs.getString("TABLE_SCHEM")),
          rs.getString("TABLE_CATALOG")
        )

        accumulator(acc :+ s)
      } else {
        acc
      }

    accumulator(Nil)

  }

}


