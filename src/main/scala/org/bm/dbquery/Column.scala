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

package org.bm.dbquery

import java.sql.{Connection, ResultSet}

import scala.annotation.tailrec

/**
 *
 * @author Baptiste Morin
 */
class Column(val catalog: Option[String],
             val schema: Option[String],
             val tableName: String,
             val name: String,
             val dataType: SqlType,
             val typeName: String,
             val size: Integer,
             val bufferLength: Integer /*Not used*/ ,
             val decimalDigits: Integer,
             val numPrecRadix: Integer,
             val nullable: Integer,
             val remarks: Option[String],
             val defaultValue: Option[String],
             val sqlDataType: Integer /*Not used*/ ,
             val sqlDatetimeSub: Integer /*Not used*/ ,
             val charOctetLength: Integer,
             val ordinalPosition: Integer /*starting at 1*/ ,
             val isNullable: String /*YES, NO, empty string*/
              ) {

  override def toString: String =
    if (SqlType.DECIMAL == dataType)
      if (size != 0 && decimalDigits != 0) {
        s"$name $typeName($size, $decimalDigits)"
      } else if (decimalDigits == 0) {
        s"$name $typeName($size)"
      } else {
        s"$name $typeName"
      }
    else
      s"$name $typeName($size)"

}

object Column {

  def apply(table: Table)(implicit conn: Connection): List[Column] =
    Column(table.name, null, table.catalog match { case Some(c) => c; case None => null}, table.schema match { case Some(s) => s; case None => null})

  def apply(tableName: String = null, columnNamePattern: String = null, catalogPattern: String = null, schemaPattern: String = null)(implicit conn: Connection): List[Column] =
    Column(conn.getMetaData.getColumns(catalogPattern, schemaPattern, tableName, columnNamePattern))


  def apply(rs: ResultSet): List[Column] = {
    @tailrec
    def accumulator(acc: List[Column]): List[Column] =
      if (rs.next()) {
        val col = new Column(
          Option(rs.getString("TABLE_CAT")),
          Option(rs.getString("TABLE_SCHEM")),
          rs.getString("TABLE_NAME"),
          rs.getString("COLUMN_NAME"),
          SqlType.from(rs.getInt("DATA_TYPE")),
          rs.getString("TYPE_NAME"),
          rs.getInt("COLUMN_SIZE"),
          rs.getInt("BUFFER_LENGTH"),
          rs.getInt("DECIMAL_DIGITS"),
          rs.getInt("NUM_PREC_RADIX"),
          rs.getInt("NULLABLE"),
          Option(rs.getString("REMARKS")),
          Option(rs.getString("COLUMN_DEF")),
          rs.getInt("SQL_DATA_TYPE"),
          rs.getInt("SQL_DATETIME_SUB"),
          rs.getInt("CHAR_OCTET_LENGTH"),
          rs.getInt("ORDINAL_POSITION"),
          rs.getString("IS_NULLABLE"))

        accumulator(acc :+ col)
      } else {
        acc
      }

    accumulator(Nil)
  }


}

