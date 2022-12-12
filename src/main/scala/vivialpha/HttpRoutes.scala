package vivialpha

import java.nio.charset.StandardCharsets
import java.io.{File, FileWriter}
import java.nio.file.{Files, Paths}
import scala.io.Source
import vivialpha.model.Http._
import compilersandbox.tokenizer.{Preprocessor, Tokenizer}
import compilersandbox.parser.{Node, Operand, Operator, Parser}
import compilersandbox.makeErrorMessage
import vivialpha.Template.loadTemplate

import scala.util.Left

object HttpRoutes {

  def handleResult(httpRequest: HttpRequest): HttpResponse = {

    val content = httpRequest.body.get.content
    val fileContent = content.split("=")

    val historyDir = new File("data")
    historyDir.mkdirs()
    val historyFile = new File(historyDir,"history.txt")
    historyFile.createNewFile()
    val source = Source.fromFile(historyFile)

    if (fileContent.length < 2) {
      HttpResponse(HttpStatus(400, "Bad Request"), List.empty, Body("empty expression\ncannot compute value of empty expression"))
    }
    else {
      val responseContent = fileContent(1)

      val tokens = Tokenizer.tokenize(responseContent)
      tokens match {
        case Left(failure) =>
          HttpResponse(HttpStatus(400, "Bad Request"), List.empty, Body(s"tokenizer failure: ${failure.message}"))
        case Right(tokens) =>
          val preprocessedTokens = Preprocessor.preprocess(tokens, List.empty)
          val tree = Parser.parse(preprocessedTokens)
          tree match {
            case Left(failure) =>
              HttpResponse(HttpStatus(400, "Bad Request"), List.empty, Body(s"parsing failure: ${failure.message}"))
            case Right(tree) =>

              val ast = json.Encoder.encode(tree)
              
              val result = tree.compute()

              val newFileContent = s"$responseContent=$result\n" + source.mkString
              Files.write(Paths.get("data/history.txt"), newFileContent.getBytes(StandardCharsets.UTF_8))
              source.close()

              HttpResponse(HttpStatus(200, "OK"), List.empty, Body(s"$responseContent=$result\n"))
          }
      }
    }
  }

  def handleHistory(httpRequest: HttpRequest): HttpResponse = {
    println("handling history...")
    val historyFile = new File("data/history.txt")
    historyFile.createNewFile()
    val source = Source.fromFile(historyFile)

    val fileContent = source.getLines().toList
      .map({ line => s"<p style='color: red;'>$line</p>" })
      .mkString("\n")
    source.close()

    val templateResult = loadTemplate("web/history.html", Map(
      "history" -> fileContent
    ))
    templateResult match {
      case Left(error) =>
        HttpResponse(HttpStatus(500, "Internal Server Error"), List.empty, Body(error.message))
      case Right(content) =>
        HttpResponse(HttpStatus(200, "OK"), List.empty, Body(content))
    }
  }

  def handleClear(httpRequest: HttpRequest): HttpResponse = {

    val historyFile = File("data/history.txt")
    if (historyFile.exists() && historyFile.isFile) {
      Files.write(historyFile.toPath, "".getBytes(StandardCharsets.UTF_8))
    }

    val templateResult = loadTemplate("web/successful.html", Map(
      "successful action" -> "cleared history"
    ))
    templateResult match {
      case Left(error) =>
        HttpResponse(HttpStatus(500, "Internal Server Error"), List.empty, Body(error.message))
      case Right(content) =>
        HttpResponse(HttpStatus(200, "OK"), List.empty, Body(content))
    }
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
      notFound(sourceFile.getPath)
    }
  }

  def badRequest(error: String, details: String): HttpResponse = {
    val templateResult = loadTemplate("web/error.html", Map(
      "error" -> error,
      "details" -> details
    ))
    templateResult match {
      case Left(error) =>
        HttpResponse(HttpStatus(500, "Internal Server Error"), List.empty, Body(error.message))
      case Right(content) =>
        HttpResponse(HttpStatus(400, "Bad Request"), List.empty, Body(content))
    }
  }

  def notFound(filePath: String): HttpResponse = {
    val templateResult = loadTemplate("web/error.html", Map(
      "error" -> "File not found:",
      "details" -> filePath
    ))
    templateResult match {
      case Left(error) =>
        HttpResponse(HttpStatus(500, "Internal Server Error"), List.empty, Body(error.message))
      case Right(content) =>
        HttpResponse(HttpStatus(404, "Not Found"), List.empty, Body(content))
    }
  }
}
