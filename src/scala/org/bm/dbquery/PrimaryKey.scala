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
class PrimaryKey(val catalog: Option[String],
                 val schema: Option[String],
                 val tableName: String,
                 val columnName: String,
                 val keySeq: Short,
                 val pkName: Option[String]
                  ) extends Key {

  override def toString: String = pkName match {
    case Some(name) => name
    case None => ""
  }

  override def detailledToString: String = toString + s"($columnName)"
}

object PrimaryKey {

  def apply(tableName: String, catalog: String = null, schema: String = null)(implicit conn: Connection): List[PrimaryKey] =
    PrimaryKey(conn.getMetaData.getPrimaryKeys(catalog, schema, tableName))

  def apply(table: Table)(implicit conn: Connection): List[PrimaryKey] =
    PrimaryKey(table.name, table.catalog match { case Some(c) => c; case None => null}, table.schema match { case Some(s) => s; case None => null})

  def apply(rs: ResultSet): List[PrimaryKey] = {
    @tailrec
    def accumulator(acc: List[PrimaryKey]): List[PrimaryKey] = {
      if (rs.next()) {
        val pk = new PrimaryKey(
          Option(rs.getString("TABLE_CAT")),
          Option(rs.getString("TABLE_SCHEM")),
          rs.getString("TABLE_NAME"),
          rs.getString("COLUMN_NAME"),
          rs.getShort("KEY_SEQ"),
          Option(rs.getString("PK_NAME"))
        )
        accumulator(acc :+ pk)
      } else {
        acc
      }
    }

    accumulator(Nil)
  }
}


