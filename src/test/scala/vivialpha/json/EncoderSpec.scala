package vivialpha.json

import compilersandbox.parser.{Add, DecimalOperand, IntegerOperand, Mul, Operand, OperandNode, OperatorNode}
import compilersandbox.tokenizer.Tokenizer.Operator
import org.junit.runner.RunWith
import org.scalatest.freespec.AnyFreeSpec
import org.scalatestplus.junit.JUnitRunner
import vivialpha.model.Http.*

@RunWith(classOf[JUnitRunner])
class EncoderSpec extends AnyFreeSpec {

  "A Encoder" - {

    "should encode a simple OperandNode" in {

      val input = OperandNode(IntegerOperand(5))
      val expected =
        """
          |{
          | "name": "5"
          |}
          |""".stripMargin.trim().replaceAll("\\s", "")
      val result = Encoder.encode(input)
      assert(result == expected)
    }

    "should encode a simple OperatorNode" in {

      val input = OperatorNode(Add, OperandNode(IntegerOperand(5)), OperandNode(IntegerOperand(1)))
      val expected =
        """
          |{
          | "name": "+",
          |   "children": [
          |     {"name": "5"},
          |     {"name": "1"}
          |   ]
          |}
          |""".stripMargin.trim().replaceAll("\\s", "")
      val result = Encoder.encode(input)
      assert(result == expected)
    }

    "should encode an OperatorNode" in {

      val input = OperatorNode(Add, OperatorNode(Mul, OperandNode(DecimalOperand(5.0)), OperandNode(IntegerOperand(3))), OperandNode(IntegerOperand(1)))
      val expected =
        """
          |{
          |   "name": "+",
          |   "children": [
          |     {
          |       "name": "*",
          |       "children": [
          |         {"name": "5.0"},
          |         {"name": "3"}
          |       ]
          |     },
          |     {"name": "1"}
          |   ]
          |}
          |""".stripMargin.trim().replaceAll("\\s", "")
      val result = Encoder.encode(input)
      println(result)
      assert(result == expected)
    }
  }
}
