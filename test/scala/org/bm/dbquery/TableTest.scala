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

import java.sql.{Connection, DriverManager}

import org.bm.dbquery.dsl.query.{*, select}
import org.bm.dbquery.utils.ResultSetDumper
import org.bm.dbquery.utils.WithResource.withResource
import org.scalatest.FunSuite

import scala.language.postfixOps

/**
 *
 * @author Baptiste Morin
 */
class TableTest extends FunSuite {

  val url = "jdbc:h2:mem:"
  val username: String = "sa"
  val password: String = ""

  import org.bm.dbquery.utils.ResultSetDumper.Implicits._


  test("test table list") {

//    Class.forName("oracle.jdbc.OracleDriver")

    implicit val conn: Connection = DriverManager.getConnection(url, username, password)
    withResource(conn) {
      val tables: List[Table] = Table("NUMBER_TEST", schemaPattern = username)

      tables foreach { tab =>
        println(tab)

        val cols: List[Column] = Column(tab.name, schemaPattern = username)

        cols foreach { col =>
          println(s"\t$col")
        }
      }
    }
  }

  test("ResultSetDumper") {
//    Class.forName("oracle.jdbc.OracleDriver")

    val conn: Connection = DriverManager.getConnection(url, username, password)
    withResource(conn) {
      println(

        ResultSetDumper.format(
          ResultSetDumper.dump(
            conn.getMetaData.getSchemas(null, null)
          )
        )
      )
    }
  }

  test("API") {
//    Class.forName("oracle.jdbc.OracleDriver")
    implicit val conn: Connection = DriverManager.getConnection(url, username, password)

    withResource(conn) {
      conn.createStatement().execute("CREATE TABLE BDOMO_RFDEJCL(id number(15), name varchar2(32))")


      println("Primary Key")
      println(ResultSetDumper.format(ResultSetDumper.dump(conn.getMetaData.getPrimaryKeys(null, "sa", "BDOMO_RFDEJCL"))))

      println("Imported Keys")
      println(ResultSetDumper.format(ResultSetDumper.dump(conn.getMetaData.getImportedKeys(null, "sa", "BDOMO_RFDEJCL"))))

      println("Exported Keys")
      println(ResultSetDumper.format(ResultSetDumper.dump(conn.getMetaData.getExportedKeys(null, "sa", "BDOMO_RFDEJCL"))))

      val tables = Table("BDOMO_RFDEJCL", schemaPattern = "sa")
      val pk = PrimaryKey(tables.head)
      val fk = ForeignKey(tables.head)

      println(s"table: ${tables.head}\nPK: ${pk mkString ", "}\nFK: ${fk.map(foreignKey => foreignKey.detailledToString) mkString ", "}")

    }

  }

  test("All tables") {
//    Class.forName("oracle.jdbc.OracleDriver")

    implicit val conn: Connection = DriverManager.getConnection(url, username, password)

    withResource(conn) {
      val schemas = Schema(schemaNamePattern = username)
      println(s"There are ${schemas.size} schemas.")
      schemas foreach { schema =>
        println(schema)

        val tables = Table(schemaPattern = schema.name, tableNamePattern = "BATCH_KBC%")
        println(s"There are ${tables.size} tables.")
        tables foreach { table =>
          println(s"\t- $table")

          val columns = Column(table)
          val primaryKeys = PrimaryKey(table)
          val foreignKeys = ForeignKey(table)

          println("Primary Keys: ")
          primaryKeys foreach { k =>
            println(k.detailledToString)
          }

          println("Foreign Keys: ")
          foreignKeys foreach { k =>
            println(k.detailledToString)
          }
          println("Columns")
          columns foreach println

        }
        println("----------")
        println()

      }
      println("----------")
      println()
    }

    assert(conn.isClosed)
  }

  test("mapping") {
    implicit val conn: Connection = DriverManager.getConnection(url, username, password)
    withResource(conn) {
      val b1 = conn.createStatement().executeUpdate("DROP TABLE IF EXISTS TEST_TABLE")
      val b2 = conn.createStatement().executeUpdate(
        """
          |CREATE TABLE TEST_TABLE (
          |  id INT,
          |  name VARCHAR(32),
          |  creation_date TIMESTAMP
          |)
        """.stripMargin)

      val b3 = conn.createStatement().executeUpdate("INSERT INTO TEST_TABLE(id, name, creation_date) VALUES (1, 'name 1', CURRENT_TIMESTAMP())")
      
      val rs = conn.createStatement().executeQuery("select * from TEST_TABLE")
      rs.next
      val tt = TestTable(rs)

      assert(tt.id.isDefined && tt.id.get === 1)
      assert(tt.name.isDefined && tt.name.get === "name 1")
      
    }
  }

}
