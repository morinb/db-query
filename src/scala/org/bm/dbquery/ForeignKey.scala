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
class ForeignKey(val pkTableCatalog: Option[String],
                 val pkTableSchema: Option[String],
                 val pkTableName: String,
                 val pkColumnName: String,
                 val fkTableCatalog: Option[String],
                 val fkTableSchema: Option[String],
                 val fkTableName: String,
                 val fkColumnName: String,
                 val keySeq: Short,
                 val updateRule: KeyRule,
                 val deleteRule: KeyRule,
                 val fkName: Option[String],
                 val pkName: Option[String],
                 val deferrability: KeyRule
                  ) extends Key {
  override def toString: String =
    s"${
      fkName match {
        case Some(s) => s
        case None => ""
      }
    }"

  override def detailledToString: String = toString +
    s" $fkColumnName -> $pkTableName($pkColumnName)"
}

object ForeignKey {

  def referencingKeys(conn: Connection, table: Table): List[ForeignKey] =
    referencingKeys(conn, table.name, table.catalog match { case Some(c) => c; case None => null}, table.schema match { case Some(s) => s; case None => null})

  def referencingKeys(conn: Connection, tableName: String, catalog: String = null, schema: String = null): List[ForeignKey] =
    ForeignKey(conn.getMetaData.getExportedKeys(catalog, schema, tableName))

  def apply(conn: Connection, table: Table): List[ForeignKey] =
    ForeignKey(conn, table.name, table.catalog match { case Some(c) => c; case None => null}, table.schema match { case Some(s) => s; case None => null})

  def apply(conn: Connection, tableName: String, catalog: String = null, schema: String = null): List[ForeignKey] =
    ForeignKey(conn.getMetaData.getImportedKeys(catalog, schema, tableName))

  def apply(rs: ResultSet): List[ForeignKey] = {

    @tailrec
    def accumulator(acc: List[ForeignKey]): List[ForeignKey] = {
      if (rs.next()) {
        val ik = new ForeignKey(
          Option(rs.getString("PKTABLE_CAT")),
          Option(rs.getString("PKTABLE_SCHEM")),
          rs.getString("PKTABLE_NAME"),
          rs.getString("PKCOLUMN_NAME"),
          Option(rs.getString("FKTABLE_CAT")),
          Option(rs.getString("FKTABLE_SCHEM")),
          rs.getString("FKTABLE_NAME"),
          rs.getString("FKCOLUMN_NAME"),
          rs.getShort("KEY_SEQ"),
          KeyRule.from(rs.getShort("UPDATE_RULE")),
          KeyRule.from(rs.getShort("DELETE_RULE")),
          Option(rs.getString("FK_NAME")),
          Option(rs.getString("PK_NAME")),
          KeyRule.from(rs.getShort("DEFERRABILITY"))
        )
        accumulator(acc :+ ik)
      } else {
        acc
      }
    }

    accumulator(Nil)

  }

}
