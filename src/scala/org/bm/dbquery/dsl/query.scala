/*
 * Copyright (c) 2015. Baptiste MORIN (408658)
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

import java.sql.{Connection, ResultSet}

import scala.language.implicitConversions

/**
 *
 * @author Baptiste Morin
 */
object query {
  implicit def stringToQueryElem(s: String): QueryElem = Static(s)

  implicit def asteriskToQueryElemList(value: *.type): List[QueryElem] = List(value)

  case class Select(params: List[QueryElem])(implicit conn: Connection) {
    def from(s: String): From = From(this, s)
  }

  object select {
    def apply(value: *.type)(implicit conn: Connection): Select = Select(value)
  }

  case class From(select: Select, tableName: String)(implicit conn: Connection) {
    def where(conditions: List[ConditionElem]): Where = Where(this, conditions)

    def order_by(params: List[QueryElem]): OrderBy = OrderBy(Where(this, Nil), params)
  }

  case class Where(from: From, conditions: List[ConditionElem])(implicit conn: Connection) extends Executable {

    def and(conditionElem: List[ConditionElem]): Where = Where(from, conditions ::: conditionElem)

    def order_by(orderByElems: List[QueryElem]): OrderBy = OrderBy(Where(from, Nil), orderByElems)

    override def execute[T](f: ResultSet => List[T]): List[T] = executeQuery(f, this)
  }

  case class OrderBy(where: Where, params: List[QueryElem]) extends Executable {
    override def execute[T](map: (ResultSet) => List[T]): List[T] = Nil
  }

  private def executeQuery[T](map: ResultSet => List[T], where: Where)(implicit conn: Connection): List[T] = {
    val sb: StringBuilder = new StringBuilder()

    sb ++= "select " + where.from.select.params.mkString(", ") + " from " + where.from.tableName
    if (where.conditions.nonEmpty) {
      sb ++= " where " + where.conditions.mkString(" and ")
    }


    println(sb.toString())

    map(conn.prepareStatement(sb.toString()).executeQuery())
  }


  case class GroupBy(params: List[QueryElem]) extends Executable {
    override def execute[T](map: (ResultSet) => List[T]): List[T] = Nil
  }

  trait Executable {
    def execute[T](map: ResultSet => List[T]): List[T]
  }

  implicit def conditionElemToList(c: ConditionElem): List[ConditionElem] = List(c)

  implicit def stringToConditionElem(s: String): List[ConditionElem] =
    if (isParameterCharPresent(s)) {
      ParametredCondition(s)
    } else {
      StaticCondition(s)
    }

  private def isParameterCharPresent(s: String): Boolean = false

  sealed trait ConditionElem

  case class StaticCondition(s: String) extends ConditionElem {
    override def toString = s
  }

  case class ParametredCondition(s: String) extends ConditionElem {
    override def toString = s
  }


  sealed trait QueryElem {
    def +(elem: List[QueryElem]) = this :: elem
  }

  case object * extends QueryElem

  case class Static(name: String) extends QueryElem

}
