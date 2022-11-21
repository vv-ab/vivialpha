package vivialpha

import org.junit.runner.RunWith
import org.scalatest.freespec.AnyFreeSpec
import org.scalatestplus.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class HttpEncoderSpec extends AnyFreeSpec {

  "A HttpEncoder" - {

    "should encode a HttpResponse" in {

      val expectation = Right(
        "HTTP/1.1 200 OK\r\n" +
        "Content-Encoding: gzip\r\n" +
        "Content-Length: 648\r\n" +
        "\r\n")

      val response = HttpResponse(
        HttpStatus(200, "OK"),
        List(
          Header("Content-Encoding", "gzip"),
          Header("Content-Length", "648")
        ),
        Body("")
      )

      assert(HttpEncoder.encode(response) == expectation)
    }

    "should encode a HttpResponse with body" in {

      val expectation = Right(
        "HTTP/1.1 400 Bad Request\r\n" +
        "Content-Encoding: gzip\r\n" +
        "Content-Length: 648\r\n" +
        "\r\n" +
        "hello world")

      val response = HttpResponse(
        HttpStatus(400, "Bad Request"),
        List(
          Header("Content-Encoding", "gzip"),
          Header("Content-Length", "648")
        ),
        Body("hello world")
      )

      assert(HttpEncoder.encode(response) == expectation)
    }
  }
}