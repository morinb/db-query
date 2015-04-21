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

package org.bm.dbquery.dsl

import java.sql.{Connection, DriverManager, ResultSet}

import org.bm.dbquery.dsl.query.{*, select}
import org.scalatest.FunSuite

/**
 *
 * @author Baptiste Morin
 */
class DslQueryTest extends FunSuite {

  val driver = "org.h2.Driver"
  val url = "jdbc:h2:mem:"
  val username = "sa"
  val password = ""

  Class.forName(driver)

  implicit val connection: Connection = DriverManager.getConnection(url, username, password)


  test("DSL") {

    val res = select(*) from "dual" where "1=1" and "1!=2" execute map

    res foreach { ls =>
      ls foreach { l =>
        print(" | " + l)
      }
      println()
    }


  }

  test("Close connection") {
    connection.close()
  }


  def map(rs: ResultSet): List[List[String]] = {
    val columnNumber = rs.getMetaData.getColumnCount
    val names: Array[String] = new Array[String](columnNumber)

    for {
      index <- 1 to columnNumber - 1
    } yield names(index) = rs.getMetaData.getColumnName(index)




    def extract(): List[String] = {
      val res: Array[String] = new Array[String](columnNumber)

      for {
        index <- 1 to columnNumber - 1
      } yield res(index) = rs.getString(index)

      res.toList
    }


    def acc(accu: List[List[String]] = Nil): List[List[String]] = {
      if (rs.next()) {
        val extracted = extract()
        acc(extracted :: accu)
      } else {
        accu
      }
    }


    names.toList :: acc()
  }


}
