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

import java.sql.ResultSet

import org.apache.commons.lang.StringUtils

import scala.annotation.tailrec

/**
 *
 * @author Baptiste Morin
 */
object ResultSetDumper {

  object Implicits {
    implicit lazy val MaxLength = None
  }

  def dump(rs: ResultSet): List[List[String]] = {
    var result: List[List[String]] = List()

    val colNumber = rs.getMetaData.getColumnCount


    @tailrec
    def getDatas(acc: List[String], index: Int)(getFunction: (Int => String)): List[String] = {
      if (index == 0) {
        acc
      } else {
        getDatas(getFunction(index) :: acc, index - 1)(getFunction)
      }
    }

    result :+= getDatas(Nil, colNumber)(rs.getMetaData.getColumnName)

    while (rs.next()) {
      result :+= getDatas(Nil, colNumber)(rs.getString)
    }


    result
  }

  def format(rows: List[List[String]])(implicit maxLength: Option[Int]): String = {
    val maxColumnSize: Array[Int] = new Array[Int](rows.head.size)

    // iterate over the list to compute max column length.
    for ((cols, rowIndex) <- rows.view.zipWithIndex) {
      for ((col, colIndex) <- cols.view.zipWithIndex) {
        if (col != null) {
          if (col.length > maxColumnSize(colIndex)) {

            val size = maxLength match {
              case Some(l) => l
              case None => col.length
            }

            maxColumnSize(colIndex) = Math.min(col.length, size)
          }
        }
      }
    }

    val lineLength = maxColumnSize.sum + 3 * maxColumnSize.length

    val sb = new StringBuilder(lineLength)

    for ((cols, rowIndex) <- rows.view.zipWithIndex) {
      val resultRows: Array[String] = new Array[String](rows.size)

      for ((col, colIndex) <- cols.view.zipWithIndex) {
        val txt = if (col == null) "null" else col

        val size = maxLength match {
          case Some(length) => length
          case None => maxColumnSize(colIndex)
        }

        val realText: String =
          if (txt.length > size)
            StringUtils.abbreviate(txt, size)
          else if (txt.length < size)
            txt.padTo(maxColumnSize(colIndex), ' ')
          else txt


        sb ++= " " ++ realText
        sb ++= " |"
      }

      if (rowIndex == 0) {
        sb ++= "\n"
        sb ++= "".padTo(lineLength, '-')
      }

      sb ++= "\n"

    }

    sb.toString()
  }

}
