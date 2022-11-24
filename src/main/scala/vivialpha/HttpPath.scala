package vivialpha

import java.nio.charset.StandardCharsets
import java.io.{File, FileWriter}
import java.nio.file.{Files, Paths}
import scala.io.Source
import vivialpha.model.Http.*
import compilersandbox.tokenizer.{Preprocessor, Tokenizer}
import compilersandbox.parser.{Node, Operand, Operator, Parser}
import compilersandbox.makeErrorMessage

object HttpPath {

  def handleJokes1(httpRequest: HttpRequest): HttpResponse = {

    HttpResponse(HttpStatus(200, "OK"), List.empty, Body("<h3 style='font-family: Arial; .center-screen:display: flex; flex-direction: column; justify-content: center; align-items: center; text-align: center;'>What is the name of penguin's fav aunt? ...Aunt Arctica</h3>"))
  }

  def handleJokes2(httpRequest: HttpRequest): HttpResponse = {

    HttpResponse(HttpStatus(200, "OK"), List.empty, Body("<h3 style='font-family: Arial; .center-screen:display: flex; flex-direction: column; justify-content: center; align-items: center; text-align: center;'>What did one ocean say to the other? Nothing, they just waved.</h3>"))
  }

  def handleVivi(httpRequest: HttpRequest): HttpResponse = {

    HttpResponse(HttpStatus(200, "OK"), List.empty, Body("<h1 style='font-family: Arial;'>Hey I'm Vivi</h1>"))
  }

  def handleHello(httpRequest: HttpRequest): HttpResponse = {

    // goToPath(???)
    // val pageContent = ???
    // replace("", pageContent)
    // HttpResponse(pageContent)

    val content = httpRequest.body.get.content
    val fileContent = content.split("=")

    val source = Source.fromFile("history.txt")
    val responseContent = fileContent(1)


    val tokens = Tokenizer.tokenize(responseContent)
    tokens match {
      case Left(failure) =>
        HttpResponse(HttpStatus(500, "Failure"), List.empty, Body(s"<div style='font-family: Arial;'>Failure<a href='index.html'>return</a></div>"))
      case Right(tokens) =>
        val preprocessedTokens = Preprocessor.preprocess(tokens, List.empty)
        val tree = Parser.parse(preprocessedTokens)
        tree match {
          case Left(failure) =>
            HttpResponse(HttpStatus(500, "Failure"), List.empty, Body(s"<div style='font-family: Arial;'><p>Failure</p><a href='index.html'>return</a></div>"))
          case Right(tree) =>
            val result = tree.compute()

            val newFileContent = s"$responseContent=$result\n" + source.mkString
            Files.write(Paths.get("history.txt"), newFileContent.getBytes(StandardCharsets.UTF_8))
            source.close()

            HttpResponse(HttpStatus(200, "OK"), List.empty, Body(s"<div style='font-family: Arial;'><h1>${fileContent(1)}=$result</h1><a href='index.html'>return</a></div>"))
        }
    }
  }

  def handleHistory(httpRequest: HttpRequest): HttpResponse = {

    val source = Source.fromFile("history.txt")
    val fileContent = source.getLines().toList
      .map({ line => s"<p style='color: red;'>$line</p>" })
      .mkString("\n")
    source.close()

    val template = Source.fromFile("web/history.html")
    val responseBody = template.mkString.replace("{{history}}", fileContent)

    HttpResponse(HttpStatus(200, "OK"), List.empty, Body(responseBody))
  }

  def handleClear(httpRequest: HttpRequest): HttpResponse = {

    Files.write(Paths.get("history.txt"), "".getBytes(StandardCharsets.UTF_8))
    HttpResponse(HttpStatus(200, "OK"), List.empty, Body("<div><p style='font-family:Arial;'>cleared history</p><a href='index.html'>return</a></div>"))
  }

  def handleStaticContent(httpRequest: HttpRequest): HttpResponse = {

    val webRootDirectory = new File("web/")
    val sourceFile = new File(webRootDirectory, httpRequest.uri.value)
    if (sourceFile.exists() && sourceFile.isFile) {
      val source = Source.fromFile(sourceFile)
      val responseBody = source.mkString
      HttpResponse(HttpStatus(200, "OK"), List.empty, Body(responseBody))
    }
    else {
      HttpResponse(HttpStatus(404, "Not Found"), List.empty, Body(s"<p style='font-family: Arial;'>File not found: ${sourceFile.getPath}</p>"))
    }
  }


  def goToPath(): Unit = {
    
    ???
  }
}
