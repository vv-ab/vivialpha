package vivialpha.json

import compilersandbox.parser.{Add, Operand, OperandNode, OperatorNode}
import compilersandbox.tokenizer.Tokenizer.Operator
import org.junit.runner.RunWith
import org.scalatest.freespec.AnyFreeSpec
import org.scalatestplus.junit.JUnitRunner
import vivialpha.model.Http.*

@RunWith(classOf[JUnitRunner])
class EncoderSpec extends AnyFreeSpec {

  "A Encoder" - {

    "should encode a simple OperandNode" in {

      val input = OperandNode(Operand(5))
      val expected =
        """
          |{
          | name: "5.0"
          |}
          |""".stripMargin.trim().replaceAll("\\s", "")
      println(expected)
      val result = Encoder.encode(input)
      assert(result == expected)
    }

    "should encode a simple OperatorNode" in {

      val input = OperatorNode(Add, OperandNode(Operand(5)), OperandNode(Operand(1)))
      val expected =
        """
          |{
          | name: "+",
          |   children: [
          |     {name: "5.0"},
          |     {name: "1.0"}
          |   ]
          |}
          |""".stripMargin.trim().replaceAll("\\s", "")
      println(expected)
      val result = Encoder.encode(input)
      assert(result == expected)
    }
  }
}
