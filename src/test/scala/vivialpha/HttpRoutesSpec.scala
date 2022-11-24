package vivialpha

import org.junit.runner.RunWith
import org.scalatest.freespec.AnyFreeSpec
import org.scalatestplus.junit.JUnitRunner
import vivialpha.model.Http.*

@RunWith(classOf[JUnitRunner])
class HttpRoutesSpec extends AnyFreeSpec {

  "Routes" - {

    "should handle /jokes.html/1" in {

      val httpRequest = HttpRequest(Method.POST, URI("/"), List.empty, None)
      val expectation = HttpResponse(HttpStatus(200, "OK"), List.empty, Body("<h3 style='font-family: Arial; .center-screen:display: flex; flex-direction: column; justify-content: center; align-items: center; text-align: center;'>What is the name of penguin's fav aunt? ...Aunt Arctica</h3>"))

      assert(HttpRoutes.handleJokes1(httpRequest) == expectation)
    }

    "should handle /hello" in {

      val expectation = HttpResponse(HttpStatus(200, "OK"), List.empty, Body(s"<div style='font-family: Arial;'><h1>5+3=8.0</h1><a href='index.html'>return</a></div>"))
      val httpRequest = HttpRequest(Method.POST, URI("/hello"), List.empty, Some(Body("expression=5+3")))
      val result = HttpRoutes.handleHello(httpRequest)

      assert(result == expectation)
    }

    "should return status code 400 when input is empty" in {

      val expectation = HttpResponse(HttpStatus(400, "Bad Request"), List.empty, Body(s"<div style='font-family: Arial;'><p>Failure</p><a href='index.html'>return</a></div>"))
      val httpRequest = HttpRequest(Method.POST, URI("/hello"), List.empty, Some(Body("expression=")))
      val result = HttpRoutes.handleHello(httpRequest)

      assert(result == expectation)
    }
    
    
  }
}
