package vivialpha.json

import compilersandbox.parser.{Add, Ceil, CloseParenthesis, Cos, DecimalOperand, Div, Fac, Flo, IntegerOperand, Mul, Node, OpenParenthesis, Operand, OperandNode, OperatorNode, Parser, Pow, Round, Sin, Sqrt, Sub, Tan}
import compilersandbox.tokenizer.Tokenizer
import vivialpha.model.Http.{Body, HttpResponse}

object Encoder {

  def encode(node: Node): String = {

    def encodeOperator(operator: String, left: String, right: String): String = {

      s"{\"name\":\"$operator\",\"children\":[$left,$right]}"
    }

    node match {
      case OperandNode(operand) =>
        val value = operand match {
          case IntegerOperand(value) => s"$value"
          case DecimalOperand(value) => s"$value"
        }
        s"{\"name\":\"$value\"}"
      case OperatorNode(operator, left, right) =>
        operator match {
          case Sin =>
            encodeOperator("sin", s"${encode(left)}", s"${encode(right)}")
          case Cos =>
            encodeOperator("cos", s"${encode(left)}", s"${encode(right)}")
          case Sqrt =>
            encodeOperator("sqrt", s"${encode(left)}", s"${encode(right)}")
          case Fac =>
            encodeOperator("!", s"${encode(left)}", s"${encode(right)}")
          case Tan =>
            encodeOperator("tan", s"${encode(left)}", s"${encode(right)}")
          case Pow =>
            encodeOperator("^", s"${encode(left)}", s"${encode(right)}")
          case Div =>
            encodeOperator("/", s"${encode(left)}", s"${encode(right)}")
          case Add =>
            encodeOperator("+", s"${encode(left)}", s"${encode(right)}")
          case Sub =>
            encodeOperator("-", s"${encode(left)}", s"${encode(right)}")
          case Mul =>
            encodeOperator("*", s"${encode(left)}", s"${encode(right)}")
          case Round =>
            encodeOperator("≈", s"${encode(left)}", s"${encode(right)}")
          case Flo =>
            encodeOperator("⌊x⌋", s"${encode(left)}", s"${encode(right)}")
          case Ceil =>
            encodeOperator("⌈x⌉", s"${encode(left)}", s"${encode(right)}")
          case OpenParenthesis | CloseParenthesis =>
            ???
        }
    }

  }
}
