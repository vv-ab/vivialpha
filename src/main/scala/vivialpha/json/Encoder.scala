package vivialpha.json

import compilersandbox.parser.{Add, CloseParenthesis, Cos, Div, Fac, Mul, Node, OpenParenthesis, Operand, OperandNode, OperatorNode, Parser, Pow, Sin, Sqrt, Sub, Tan}
import compilersandbox.tokenizer.Tokenizer
import vivialpha.model.Http.{Body, HttpResponse}

object Encoder {

  def encode(node: Node): String = {

    node match {
      case OperandNode(operand) =>
        s"{name:\"${operand.value}\"}"

      case OperatorNode(operator, left, right) =>
        operator match {
          case Sin =>
            val operator = "sin"
            s"{name:\"$operator\",children:[{name:\"${left.compute()}\"},{name:\"${right.compute()}\"}]}"
          case Cos =>
            val operator = "cos"
            s"{name:\"$operator\",children:[{name:\"${left.compute()}\"},{name:\"${right.compute()}\"}]}"
          case Sqrt =>
            val operator = "sqrt"
            s"{name:\"$operator\",children:[{name:\"${left.compute()}\"},{name:\"${right.compute()}\"}]}"
          case Fac =>
            val operator = "!"
            s"{name:\"$operator\",children:[{name:\"${left.compute()}\"},{name:\"${right.compute()}\"}]}"
          case Tan =>
            val operator = "tan"
            s"{name:\"$operator\",children:[{name:\"${left.compute()}\"},{name:\"${right.compute()}\"}]}"
          case Pow =>
            val operator = "^"
            s"{name:\"$operator\",children:[{name:\"${left.compute()}\"},{name:\"${right.compute()}\"}]}"
          case Div =>
            val operator = "/"
            s"{name:\"$operator\",children:[{name:\"${left.compute()}\"},{name:\"${right.compute()}\"}]}"
          case Add =>
            val operator = "+"
            s"{name:\"$operator\",children:[{name:\"${left.compute()}\"},{name:\"${right.compute()}\"}]}"
          case Sub =>
            val operator = "-"
            s"{name:\"$operator\",children:[{name:\"${left.compute()}\"},{name:\"${right.compute()}\"}]}"
          case Mul =>
            val operator = "*"
            s"{name:\"$operator\",children:[{name:\"${left.compute()}\"},{name:\"${right.compute()}\"}]}"
        }
    }

  }
}
