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

import org.scalatest.FunSuite

/**
 *
 * @author Baptiste Morin
 */
class TableTest extends FunSuite {

  test("test table list") {
    Class.forName("oracle.jdbc.OracleDriver")

    val conn: Connection = DriverManager.getConnection("", "D408658", "D408658")

    import Table.Implicits.nullString
    val batchKBCDetail = Table(conn)("BATCH_KBC_DETAIL")


    val tab : Table= batchKBCDetail match {
      case Some(t: Table) => t
      case None => throw new RuntimeException("No table found")
    }

    println(tab.name +" ("+ tab.tableType+")")

    val cols: List[Column] = Column(tab.name, conn)(null)

    cols foreach { col =>
      println(col.name+" "+col.typeName+"["+col.size+"]")

    }


    conn.close()



  }

}
