package vivialpha

import org.junit.runner.RunWith
import org.scalatest.freespec.AnyFreeSpec
import org.scalatestplus.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class HttpDecoderSpec extends AnyFreeSpec {

  "A HttpDecoder" - {

    "should decode POST-Request" in {

      val request =
        "POST / HTTP/1.1\r\n" +
        "Host: localhost:1234\r\n" +
        "User-Agent: Mozilla/5.0\r\n" +
        "Sec-Fetch-User: ?1\r\n" +
        "\r\n" +
        "expression="
      val expectation = Right(HttpRequest(
        Method.POST,
        URI("/"),
        List(
          Header("Host", "localhost:1234"),
          Header("User-Agent", "Mozilla/5.0"),
          Header("Sec-Fetch-User", "?1")
        ),
        Some(Body("expression="))
      ))

      assert(HttpDecoder.decode(request) == expectation)
    }

    "should decode all request methods" - {

      val methods = List(Method.GET, Method.POST, Method.OPTIONS, Method.HEAD, Method.PUT, Method.DELETE, Method.TRACE, Method.CONNECT)
      val methodLiterals = List("GET", "POST", "OPTIONS", "HEAD", "PUT", "DELETE", "TRACE", "CONNECT")
      methodLiterals.zip(methods).foreach({ case (methodLiteral, method) =>
        s"$methodLiteral" in {
          val request =
            s"$methodLiteral / HTTP/1.1\r\n" +
            "Host: localhost:1234\r\n" +
            "User-Agent: Mozilla/5.0\r\n" +
            "Sec-Fetch-User: ?1\r\n" +
            "\r\n" +
            "expression="

          assert(HttpDecoder.decode(request).toOption.get.method == method)
        }
      })
    }

    "should decode a request without body" in {

      val request =
        "POST / HTTP/1.1\r\n" +
        "Host: localhost:1234\r\n" +
        "User-Agent: Mozilla/5.0\r\n" +
        "Sec-Fetch-User: ?1\r\n"
      val expectation = Right(HttpRequest(
        Method.POST,
        URI("/"),
        List(
          Header("Host", "localhost:1234"),
          Header("User-Agent", "Mozilla/5.0"),
          Header("Sec-Fetch-User", "?1")
        ),
        None
      ))

      assert(HttpDecoder.decode(request) == expectation)
    }


  }
}
