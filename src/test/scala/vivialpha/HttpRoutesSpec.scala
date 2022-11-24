package vivialpha

import org.junit.runner.RunWith
import org.scalatest.freespec.AnyFreeSpec
import org.scalatestplus.junit.JUnitRunner
import vivialpha.model.Http.*

@RunWith(classOf[JUnitRunner])
class HttpRoutesSpec extends AnyFreeSpec {

  "Routes" - {

    "should handle /clear" in {

      val httpRequest = HttpRequest(Method.POST, URI("/"), List.empty, None)

      assert(HttpRoutes.handleClear(httpRequest).status.code == 200)
    }

    "should handle /result" in {

      val expectation = HttpStatus(200, "OK")
      val httpRequest = HttpRequest(Method.POST, URI("/result"), List.empty, Some(Body("expression=5+3")))
      val result = HttpRoutes.handleResult(httpRequest)

      assert(result.status == expectation)
    }

    "should return status code 400 when input is empty" in {

      val expectation = HttpStatus(400, "Bad Request")
      val httpRequest = HttpRequest(Method.POST, URI("/hello"), List.empty, Some(Body("expression=")))
      val result = HttpRoutes.handleResult(httpRequest)

      assert(result.status == expectation)
    }


  }
}
