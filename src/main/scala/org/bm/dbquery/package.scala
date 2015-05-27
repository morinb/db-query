/*
 * Copyright (c) 2015. Baptiste MORIN
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

package org.bm

import java.sql.ResultSet

import org.joda.time.LocalDateTime

import scala.language.{reflectiveCalls, implicitConversions}

package object dbquery {
  def column[T](columnName: String)(implicit f: (ResultSet, String) => T, rs: ResultSet, columnNames: IndexedSeq[String]): Option[T] = {
    if (columnNames contains columnName.toUpperCase) {
      try {
        Some(f(rs, columnName))
      } catch {
        case _: Throwable => None
      }
    } else None
  }


  object Implicits {
    implicit def columnNames(implicit rs: ResultSet): IndexedSeq[String] =
      for {
        index <- 1 to rs.getMetaData.getColumnCount
      } yield rs.getMetaData.getColumnName(index).toUpperCase

    implicit def getObject(rs: ResultSet, columnName: String): AnyRef = rs.getObject(columnName)

    implicit def getLong(rs: ResultSet, columnName: String): Long = rs.getLong(columnName)

    implicit def getDouble(rs: ResultSet, columnName: String): Double = rs.getDouble(columnName)

    implicit def getFloat(rs: ResultSet, columnName: String): Float = rs.getFloat(columnName)

    implicit def getBigDecimal(rs: ResultSet, columnName: String): BigDecimal = rs.getBigDecimal(columnName)

    implicit def getInt(rs: ResultSet, columnName: String): Int = rs.getInt(columnName)

    implicit def getString(rs: ResultSet, columnName: String): String = rs.getString(columnName)

    implicit def getLocalDateTime(rs: ResultSet, columnName: String): LocalDateTime = {
      val date: java.util.Date = rs.getTimestamp(columnName)
      LocalDateTime.fromDateFields(date)
    }

    implicit def getBoolean(rs: ResultSet, columnName: String): Boolean = rs.getBoolean(columnName)
  }

}
