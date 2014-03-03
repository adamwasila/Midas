package com.ee.midas.dsl.expressions

import org.bson.BSONObject

@FunctionExpression
final case class Add(expressions: Expression*) extends ArithmeticFunction(expressions: _*) {
  def evaluate(document: BSONObject) = {
    val result = expressions.foldLeft(0d) { (sum, expression) =>
      sum + value(expression.evaluate(document))
    }
    Literal(result)
  }
}

@FunctionExpression
final case class Multiply(expressions: Expression*) extends ArithmeticFunction(expressions: _*) {
  def evaluate(document: BSONObject) =
    if(expressions.length == 0) {
      Literal(0)
    } else {
      val result = expressions.foldLeft(1d) { (product, expression) =>
          product * value(expression.evaluate(document))
      }
      Literal(result)
    }
}

@FunctionExpression
final case class Subtract(expressions: Expression*) extends ArithmeticFunction(expressions: _*) {
  def evaluate(document: BSONObject) =
    expressions.length match {
      case 0 | 1 => Literal(0)
      case _ =>
        val minuend = value(expressions(0).evaluate(document))
        val subtrahend = value(expressions(1).evaluate(document))
        Literal(minuend - subtrahend)
    }
}

@FunctionExpression
final case class Divide(expressions: Expression*) extends ArithmeticFunction(expressions: _*) {
  def evaluate(document: BSONObject) =
    expressions.length match {
      case 0 | 1 => Literal(0)
      case _ =>
        val dividend = value(expressions(0).evaluate(document))
        val divisor = value(expressions(1).evaluate(document))
        Literal(dividend / divisor)
    }
}

@FunctionExpression
final case class Mod(expressions: Expression*) extends ArithmeticFunction(expressions: _*) {
  def evaluate(document: BSONObject) =
    expressions.length match {
      case 0 | 1 => Literal(0)
      case _ =>
        val dividend = value(expressions(0).evaluate(document))
        val divisor = value(expressions(1).evaluate(document))
        Literal(dividend % divisor)
    }
}