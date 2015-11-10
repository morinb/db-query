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

import scala.language.{implicitConversions, reflectiveCalls}

package object dbquery {
  /**
    * Defines a mapping thanks to the 'f' function.
    * @param columnName the column name in the query.
    * @param f the mapping function.
    * @param rs the ResultSet to extract datas from.
    * @param columnNames a list of the column names.
    * @tparam T the type of the column.
    * @return the value of the column in a line of the ResultSet.
    */
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
    /**
      *
      * @param rs the resultset to extract the column names from.
      * @return the column name extracted from the result set.
      */
    implicit def columnNames(implicit rs: ResultSet): IndexedSeq[String] =
      for {
        index <- 1 to rs.getMetaData.getColumnCount
      } yield rs.getMetaData.getColumnName(index).toUpperCase

    /**
      * Gets the data represented by the column name in the resultset as an Object.
      * @param rs The ResultSet to extract the data from.
      * @param columnName The column name of the data to extract.
      * @return the data represented by the column name in the resultset as an Object.
      */
    implicit def getObject(rs: ResultSet, columnName: String): AnyRef = rs.getObject(columnName)

    /**
      * Gets the data represented by the column name in the resultset as a Long.
      * @param rs The ResultSet to extract the data from.
      * @param columnName The column name of the data to extract.
      * @return the data represented by the column name in the resultset as a Long.
      */
    implicit def getLong(rs: ResultSet, columnName: String): Long = rs.getLong(columnName)

    /**
      * Gets the data represented by the column name in the resultset as a Double.
      * @param rs The ResultSet to extract the data from.
      * @param columnName The column name of the data to extract.
      * @return the data represented by the column name in the resultset as a Double.
      */
    implicit def getDouble(rs: ResultSet, columnName: String): Double = rs.getDouble(columnName)

    /**
      * Gets the data represented by the column name in the resultset as a Float.
      * @param rs The ResultSet to extract the data from.
      * @param columnName The column name of the data to extract.
      * @return the data represented by the column name in the resultset as a Float.
      */
    implicit def getFloat(rs: ResultSet, columnName: String): Float = rs.getFloat(columnName)

    /**
      * Gets the data represented by the column name in the resultset as a BigDecimal.
      * @param rs The ResultSet to extract the data from.
      * @param columnName The column name of the data to extract.
      * @return the data represented by the column name in the resultset as a BigDecimal.
      */
    implicit def getBigDecimal(rs: ResultSet, columnName: String): BigDecimal = rs.getBigDecimal(columnName)

    /**
      * Gets the data represented by the column name in the resultset as an Int.
      * @param rs The ResultSet to extract the data from.
      * @param columnName The column name of the data to extract.
      * @return the data represented by the column name in the resultset as an Int.
      */
    implicit def getInt(rs: ResultSet, columnName: String): Int = rs.getInt(columnName)

    /**
      * Gets the data represented by the column name in the resultset as a String.
      * @param rs The ResultSet to extract the data from.
      * @param columnName The column name of the data to extract.
      * @return the data represented by the column name in the resultset as a String.
      */
    implicit def getString(rs: ResultSet, columnName: String): String = rs.getString(columnName)

    /**
      * Gets the data represented by the column name in the resultset as a LocalDateTime.
      * @param rs The ResultSet to extract the data from.
      * @param columnName The column name of the data to extract.
      * @return the data represented by the column name in the resultset as a LocalDateTime.
      */
    implicit def getLocalDateTime(rs: ResultSet, columnName: String): LocalDateTime = {
      val date: java.util.Date = rs.getTimestamp(columnName)
      LocalDateTime.fromDateFields(date)
    }

    /**
      * Gets the data represented by the column name in the resultset as a Boolean.
      * @param rs The ResultSet to extract the data from.
      * @param columnName The column name of the data to extract.
      * @return the data represented by the column name in the resultset as a Boolean.
      */
    implicit def getBoolean(rs: ResultSet, columnName: String): Boolean = rs.getBoolean(columnName)
  }

}
