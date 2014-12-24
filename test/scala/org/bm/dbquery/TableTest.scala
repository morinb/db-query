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

import java.sql.{Connection, DriverManager}

import org.bm.dbquery.utils.ResultSetDumper
import org.scalatest.FunSuite

/**
 *
 * @author Baptiste Morin
 */
class TableTest extends FunSuite {

  val url = ""

  test("test table list") {
    Class.forName("oracle.jdbc.OracleDriver")

    val conn: Connection = DriverManager.getConnection(url, "D408658", "D408658")
    val tables: List[Table] = Table(conn, "NUMBER_TEST",schemaPattern = "D408658")

    tables foreach { tab =>
      println(tab)


      val cols: List[Column] = Column(conn, tab.name, schemaPattern = "D408658")

      cols foreach { col =>
        println(s"\t$col")
      }


    }

    conn.close()
  }

  test("ResultSetDumper") {
    Class.forName("oracle.jdbc.OracleDriver")

    val conn: Connection = DriverManager.getConnection(url, "D408658", "D408658")

    implicit val maxlength = None

    println(

      ResultSetDumper.format(
        ResultSetDumper.dump(
          conn.getMetaData.getTables(null, "D408658", "NUMBER_TEST", null)
        )
      )
    )

    conn.close()
  }


}
