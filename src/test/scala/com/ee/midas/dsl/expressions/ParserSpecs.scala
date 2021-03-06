/******************************************************************************
* Copyright (c) 2014, Equal Experts Ltd
* All rights reserved.
*
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions are
* met:
*
* 1. Redistributions of source code must retain the above copyright notice,
*    this list of conditions and the following disclaimer.
* 2. Redistributions in binary form must reproduce the above copyright
*    notice, this list of conditions and the following disclaimer in the
*    documentation and/or other materials provided with the distribution.
*
* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
* "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
* TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
* PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER
* OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
* EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
* PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
* PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
* LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
* NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
* SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*
* The views and conclusions contained in the software and documentation
* are those of the authors and should not be interpreted as representing
* official policies, either expressed or implied, of the Midas Project.
******************************************************************************/

package com.ee.midas.dsl.expressions

import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

@RunWith(classOf[JUnitRunner])
class ParserSpecs extends Specification {

  trait ExpressionParser extends Parser with Scope {
    def Result[T](result: ParseResult[T]): T = result match {
      case Success(value, _) =>  value
      case NoSuccess(message, _) => throw new IllegalArgumentException(s"Parsing Failed Message: $message")
    }
  }

  "Parser" should {

    "Parse literal" in {
      "null" in new ExpressionParser {
        //Given
        val input = "null"

        //When
        val expression = Result(parseAll(value, input))

        //Then
        expression mustEqual Literal(null)
      }

      "true" in new ExpressionParser {
        //Given
        val input = "true"

        //When
        val expression = Result(parseAll(value, input))

        //Then
        expression mustEqual Literal(true)
      }

      "false" in new ExpressionParser {
        //Given
        val input = "false"

        //When
        val expression = Result(parseAll(value, input))

        //Then
        expression mustEqual Literal(false)
      }

      "Integer" in new ExpressionParser {
        //Given
        val input = "2"

        //When
        val expression = Result(parseAll(value, input))

        //Then
        expression mustEqual Literal(2)
      }

      "Decimal" in new ExpressionParser {
        //Given
        val input = "2.4"

        //When
        val expression = Result(parseAll(value, input))

        //Then
        expression mustEqual Literal(2.4)
      }

      "double quoted string" in new ExpressionParser {
        //Given
        val input = """"age""""

        //When
        val expression = Result(parseAll(quotedStringLiteral, input))

        //Then
        expression mustEqual Literal("age")
      }

      "single quoted string" in new ExpressionParser {
        //Given
        val input = """'age'"""

        //When
        val expression = Result(parseAll(singleQuotedStringLiteral, input))

        //Then
        expression mustEqual Literal("age")
      }

    }

    "Parse Double Quoted Field from" in {
      "fieldName" in new ExpressionParser {
        //Given
        val input = """"$age""""

        //When
        val expression = Result(parseAll(value, input))

        //Then
        expression mustEqual Field("age")
      }

      "level-1 nested fieldName" in new ExpressionParser {
        //Given
        val input = """"$address.zip""""

        //When
        val expression = Result(parseAll(value, input))

        //Then
        expression mustEqual Field("address.zip")
      }

      "level-2 nested fieldName" in new ExpressionParser {
        //Given
        val input = """"$address.line.1""""

        //When
        val expression = Result(parseAll(value, input))

        //Then
        expression mustEqual Field("address.line.1")
      }
    }

    "Parse Single Quoted Field" in {
      "fieldName" in new ExpressionParser {
        //Given
        val input = """'$age'"""

        //When
        val expression = Result(parseAll(singleQuotedField, input))

        //Then
        expression mustEqual Field("age")
      }

      "level-1 nested fieldName" in new ExpressionParser {
        //Given
        val input = """'$address.zip'"""

        //When
        val expression = Result(parseAll(singleQuotedField, input))

        //Then
        expression mustEqual Field("address.zip")
      }

      "level-2 nested fieldName" in new ExpressionParser {
        //Given
        val input = """'$address.line.1'"""

        //When
        val expression = Result(parseAll(singleQuotedField, input))

        //Then
        expression mustEqual Field("address.line.1")
      }
    }

    "Not Parse Double Quoted Field" in {
      "without a name" in new ExpressionParser {
        //Given
        val input = "$"

        //When-Then
        Result(parseAll(quotedField, input)) must throwA[IllegalArgumentException]
      }

      "that has trailing dot" in new ExpressionParser {
        //Given
        val input = """"$address.""""

        //When-Then
        Result(parseAll(quotedField, input)) must throwA[IllegalArgumentException]
      }

      "that has extra dot between levels" in new ExpressionParser {
        //Given
        val input = """"$address..line""""

        //When-Then
        Result(parseAll(quotedField, input)) must throwA[IllegalArgumentException]
      }

      "that begins with a dot" in new ExpressionParser {
        //Given
        val input = """"$.address""""

        //When-Then
        Result(parseAll(quotedField, input)) must throwA[IllegalArgumentException]
      }

      "that is prefixed with multiple $" in new ExpressionParser {
        //Given
        val input = """"$$address""""

        //When-Then
        Result(parseAll(quotedField, input)) must throwA[IllegalArgumentException]
      }
    }

    "Not Parse Single Quoted Field" in {
      "without a name" in new ExpressionParser {
        //Given
        val input = "$"

        //When-Then
        Result(parseAll(singleQuotedField, input)) must throwA[IllegalArgumentException]
      }

      "that has trailing dot" in new ExpressionParser {
        //Given
        val input = """'$address.'"""

        //When-Then
        Result(parseAll(singleQuotedField, input)) must throwA[IllegalArgumentException]
      }

      "that has extra dot between levels" in new ExpressionParser {
        //Given
        val input = """'$address..line'"""

        //When-Then
        Result(parseAll(singleQuotedField, input)) must throwA[IllegalArgumentException]
      }

      "that begins with a dot" in new ExpressionParser {
        //Given
        val input = """'$.address'"""

        //When-Then
        Result(parseAll(singleQuotedField, input)) must throwA[IllegalArgumentException]
      }

      "that is prefixed with multiple $" in new ExpressionParser {
        //Given
        val input = """'$$address'"""

        //When-Then
        Result(parseAll(singleQuotedField, input)) must throwA[IllegalArgumentException]
      }
    }

    "Parse Function" in {

      "from function name" in new ExpressionParser {
        //Given
        val funcName = "function"
        val input = "$" + funcName


        //When
        val functionName = Result(parseAll(fnName, input))

        //Then
        funcName mustEqual functionName
      }

      "with empty args" in new ExpressionParser {
        //Given
        val input = "[]"

        //When
        val args = Result(parseAll(fnArgs, input))

        //Then
        args must beEmpty
      }

      "with single Number argument in an array" in new ExpressionParser {
        //Given
        val input = "[1]"

        //When
        val args = Result(parseAll(fnArgs, input))

        //Then
        args mustEqual List(Literal(1))
      }

      "with Number and String args in an array" in new ExpressionParser {
        //Given
        val input = """[1, "age"]"""

        //When
        val args = Result(parseAll(fnArgs, input))

        //Then
        args mustEqual List(Literal(1), Literal("age"))
      }

      "with Number, double quoted String and double quote Field args in an array" in new ExpressionParser {
        //Given
        val input = """[1, "age", "$name"]"""

        //When
        val args = Result(parseAll(fnArgs, input))

        //Then
        args mustEqual List(Literal(1), Literal("age"), Field("name"))
      }

      "with Number, single quoted String and single quoted Field args in an array" in new ExpressionParser {
        //Given
        val input = """[1, 'age', '$name']"""

        //When
        val args = Result(parseAll(fnArgs, input))

        //Then
        args mustEqual List(Literal(1), Literal("age"), Field("name"))
      }

      "with Function arg in an array" in new ExpressionParser {
        //Given
        val input = """[{ $add: [1, "$age"]}]"""

        //When
        val args = Result(parseAll(fnArgs, input))

        //Then
        args mustEqual List(Add(Literal(1), Field("age")))
      }

      "with single function at top-level within obj" in new ExpressionParser {
        //Given
        val input = """{ $add: [1, { $multiply: [2, "$age"]}] }"""

        //When
        val objExpr = Result(parseAll(obj, input))

        //Then
        objExpr mustEqual Add(Literal(1), Multiply(Literal(2), Field("age")))
      }

      "with single Number argument" in new ExpressionParser {
        //Given
        val input = "1"

        //When
        val args = Result(parseAll(fnArgs, input))

        //Then
        args mustEqual List(Literal(1))
      }

      "with single quoted string argument" in new ExpressionParser {
        //Given
        val constant = "age"
        val input = s"'$constant'"

        //When
        val args = Result(parseAll(fnArgs, input))

        //Then
        args mustEqual List(Literal(constant))
      }

      "with double quoted string argument" in new ExpressionParser {
        //Given
        val input = """"age""""

        //When
        val args = Result(parseAll(fnArgs, input))

        //Then
        args mustEqual List(Literal("age"))
      }


      "with single quoted Field argument" in new ExpressionParser {
        //Given
        val fieldName = "name"
        val input = s"'$$$fieldName'"

        //When
        val args = Result(parseAll(fnArgs, input))

        //Then
        args mustEqual List(Field(fieldName))
      }

      "with double quoted Field argument" in new ExpressionParser {
        //Given
        val input = """"$name""""

        //When
        val args = Result(parseAll(fnArgs, input))

        //Then
        args mustEqual List(Field("name"))
      }

      "with single function argument" in new ExpressionParser {
        //Given
        val input = """{ $add : [1, 2] }"""

        //When
        val args = Result(parseAll(fnArgs, input))

        //Then
        args mustEqual List(Add(Literal(1), Literal(2)))
      }
    }

    "Not Parse Function" in {

      "without a name" in new ExpressionParser {
        //Given
        val input = "$"

        //When-Then
        Result(parseAll(fnName, input)) must throwA[IllegalArgumentException]
      }

      "with name containing reserved prefix $" in new ExpressionParser {
        //Given
        val funcName = "$function"
        val input = "$" + funcName

        //When-Then
        Result(parseAll(fnName, input)) must throwA[IllegalArgumentException]
      }

      "with incomplete args" in new ExpressionParser {
        //Given
        val input = "[1"

        //When-Then
        Result(parseAll(fnArgs, input)) must throwA[IllegalArgumentException]
      }

      "with ill-formed args" in new ExpressionParser {
        //Given
        val input = "[1]]"

        //When-Then
        Result(parseAll(fnArgs, input)) must throwA[IllegalArgumentException]
      }

      "with single function argument" in new ExpressionParser {
        //Given
        val input = """"$add : [1, 2]""""

        //When-Then
        Result(parseAll(fnArgs, input)) must throwA[IllegalArgumentException]
      }

      "with more than 1 function defined at top-level in obj" in new ExpressionParser {
        //Given
        val input = """{ $add: [1, { $multiply: [2, "$age"]}], $multiply: [] }"""

        //When-Then
        Result(parseAll(obj, input)) must throwA[IllegalArgumentException]
      }

      "empty divide function" in new ExpressionParser {
        //Given
        val input = """$divide: []"""

        //When
        Result(parseAll(fn, input)) must throwA[IllegalArgumentException]

      }

      "divide function with 1 argument" in new ExpressionParser {
        //Given
        val input = """$divide: [1.0]"""

        //When-Then
        Result(parseAll(fn, input)) must throwA[IllegalArgumentException]
      }

      "empty subtract function" in new ExpressionParser {
        //Given
        val input = """$subtract: []"""

        //When-Then
        Result(parseAll(fn, input)) must throwA[IllegalArgumentException]
      }

      "subtract function with 1 argument" in new ExpressionParser {
        //Given
        val input = """$subtract: [1.0]"""

        //When-Then
        Result(parseAll(fn, input)) must throwA[IllegalArgumentException]
      }

      "empty Mod function" in new ExpressionParser {
        //Given
        val input = """$mod: []"""

        //When-Then
        Result(parseAll(fn, input)) must throwA[IllegalArgumentException]
      }

      "function with 1 argument" in new ExpressionParser {
        //Given
        val input = """$mod: [1.0]"""

        //When-Then
        Result(parseAll(fn, input)) must throwA[IllegalArgumentException]
      }
    }

    "Parse Arithmetic Function" in {
      "Add for" in {
        "simple function" in new ExpressionParser {
          //Given
          val input = """$add: [1, "$age"]"""

          //When
          val add = Result(parseAll(fn, input))

          //Then
          add mustEqual Add(Literal(1), Field("age"))
        }

        "empty function" in new ExpressionParser {
          //Given
          val input = """$add: []"""

          //When
          val add = Result(parseAll(fn, input))

          //Then
          add mustEqual Add()
        }

        "recursive function" in new ExpressionParser {
          //Given
          val input = """$add: [1, { $add: [2, "$age"]}]"""

          //When
          val add = Result(parseAll(fn, input))

          //Then
          add mustEqual Add(Literal(1), Add(Literal(2), Field("age")))
        }
      }

      "Multiply for" in {
        "simple function" in new ExpressionParser {
          //Given
          val input = """$multiply: [1, "$age"]"""

          //When
          val multiply = Result(parseAll(fn, input))

          //Then
          multiply mustEqual Multiply(Literal(1), Field("age"))
        }

        "empty function" in new ExpressionParser {
          //Given
          val input = """$multiply: []"""

          //When
          val multiply = Result(parseAll(fn, input))

          //Then
          multiply mustEqual Multiply()
        }

        "recursive function" in new ExpressionParser {
          //Given
          val input = """$multiply: [1, { $multiply: [2, "$age"]}]"""

          //When
          val multiply = Result(parseAll(fn, input))

          //Then
          multiply mustEqual Multiply(Literal(1), Multiply(Literal(2), Field("age")))
        }
      }

      "Subtract for" in {
        "simple function" in new ExpressionParser {
          //Given
          val input = """$subtract: [1, "$age"]"""

          //When
          val subtract = Result(parseAll(fn, input))

          //Then
          subtract mustEqual Subtract(Literal(1), Field("age"))
        }

        "recursive function" in new ExpressionParser {
          //Given
          val input = """$subtract: [1, { $subtract: [2, "$age"]}]"""

          //When
          val subtract = Result(parseAll(fn, input))

          //Then
          subtract mustEqual Subtract(Literal(1), Subtract(Literal(2), Field("age")))
        }
      }

      "Divide for" in {
        "simple function" in new ExpressionParser {
          //Given
          val input = """$divide: [1, "$age"]"""

          //When
          val divide = Result(parseAll(fn, input))

          //Then
          divide mustEqual Divide(Literal(1), Field("age"))
        }

        "recursive function" in new ExpressionParser {
          //Given
          val input = """$divide: [1, { $divide: [2, "$age"]}]"""

          //When
          val divide = Result(parseAll(fn, input))

          //Then
          divide mustEqual Divide(Literal(1), Divide(Literal(2), Field("age")))
        }
      }

      "Mod for" in {
        "simple function" in new ExpressionParser {
          //Given
          val input = """$mod: [1, "$age"]"""

          //When
          val modulus = Result(parseAll(fn, input))

          //Then
          modulus mustEqual Mod(Literal(1), Field("age"))
        }

        "recursive function" in new ExpressionParser {
          //Given
          val input = """$mod: [1, { $mod: [2, "$age"]}]"""

          //When
          val modulus = Result(parseAll(fn, input))

          //Then
          modulus mustEqual Mod(Literal(1), Mod(Literal(2), Field("age")))
        }
      }
    }

    "Parse String Function" in {
      "concat" in new ExpressionParser {
        //Given
        val input = """$concat: [1, "$age"]"""

        //When
        val concat = Result(parseAll(fn, input))

        //Then
        concat mustEqual Concat(Literal(1), Field("age"))
      }

      "empty concat" in new ExpressionParser {
        //Given
        val input = """$concat: []"""

        //When
        val concat = Result(parseAll(fn, input))

        //Then
        concat mustEqual Concat()
      }

      "recursive concat" in new ExpressionParser {
        //Given
        val input = """$concat: [1, { $concat: [2, "$age"]}]"""

        //When
        val concat = Result(parseAll(fn, input))

        //Then
        concat mustEqual Concat(Literal(1), Concat(Literal(2), Field("age")))
      }
    }
  }
}