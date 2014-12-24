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
  val username: String = ""
  val password: String = ""

  implicit val maxlength = None

  test("test table list") {
    Class.forName("oracle.jdbc.OracleDriver")

    val conn: Connection = DriverManager.getConnection(url, username, password)
    val tables: List[Table] = Table(conn, "NUMBER_TEST", schemaPattern = username)

    tables foreach { tab =>
      println(tab)


      val cols: List[Column] = Column(conn, tab.name, schemaPattern = username)

      cols foreach { col =>
        println(s"\t$col")
      }


    }

    conn.close()
  }

  test("ResultSetDumper") {
    Class.forName("oracle.jdbc.OracleDriver")

    val conn: Connection = DriverManager.getConnection(url, username, password)



    println(

      ResultSetDumper.format(
        ResultSetDumper.dump(
          conn.getMetaData.getTables(null, username, "NUMBER_TEST", null)
        )
      )
    )

    conn.close()
  }

  test("API") {
    Class.forName("oracle.jdbc.OracleDriver")
    val conn: Connection = DriverManager.getConnection(url, username, password)


    println("Primary Key")
    println(ResultSetDumper.format(ResultSetDumper.dump(conn.getMetaData.getPrimaryKeys(null, "D408658", "BATCH_KBC_DETAIL"))))

    println("Imported Keys")
    println(ResultSetDumper.format(ResultSetDumper.dump(conn.getMetaData.getImportedKeys(null, "D408658", "BATCH_KBC_DETAIL"))))

    println("Exported Keys")
    println(ResultSetDumper.format(ResultSetDumper.dump(conn.getMetaData.getExportedKeys(null, "D408658", "BATCH_KBC_DETAIL"))))

    val tables = Table(conn, "BATCH_KBC_DETAIL", schemaPattern = "D408658")
    val pk = PrimaryKey(conn, tables(0))
    val fk = ForeignKey(conn, tables(0))

    println(s"table: ${tables(0)}\nPK: ${pk mkString ", "}\nFK: ${fk.map( foreignkey => foreignkey.detailledToString) mkString ", "}")


    conn.close()

  }


}
