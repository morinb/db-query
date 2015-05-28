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

package org.bm.dbquery

import java.sql.ResultSet

import org.joda.time.LocalDateTime

class TestTable(val id: Option[Int],
                val name: Option[String],
                val creationDate: Option[LocalDateTime]
                 ) {
  override def toString: String =
    s"""
       |ID = ${this.id.getOrElse("null")}
        |NAME = ${this.name.getOrElse("null")}
        |CREATION_DATE = ${this.creationDate.getOrElse("null")}
     """.stripMargin
}

object TestTable {


  import Implicits._

  def apply(implicit resultSet: ResultSet): TestTable = new TestTable(column("id"), column("name"), column("creation_date")
  )
}


