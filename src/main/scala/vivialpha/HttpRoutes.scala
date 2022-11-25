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

    val content = httpRequest.body.get.content // TODO: Handle empty body
    val fileContent = content.split("=")
    val source = Source.fromFile("history.txt")

    if (fileContent.length < 2) {
      badRequest("empty expression", "cannot compute value of empty expression")
    }
    else {
      val responseContent = fileContent(1)

      val tokens = Tokenizer.tokenize(responseContent)
      tokens match {
        case Left(failure) =>
          badRequest("tokenizer failure", failure.message)
        case Right(tokens) =>
          val preprocessedTokens = Preprocessor.preprocess(tokens, List.empty)
          val tree = Parser.parse(preprocessedTokens)
          tree match {
            case Left(failure) =>
              badRequest("parsing failure", failure.message)
            case Right(tree) =>
              val result = tree.compute()

              val newFileContent = s"$responseContent=$result\n" + source.mkString
              Files.write(Paths.get("history.txt"), newFileContent.getBytes(StandardCharsets.UTF_8))
              source.close()

              val templateResult = loadTemplate("web/successful.html", Map(
                "successful action" -> s"$responseContent=$result\n"
              ))
              templateResult match {
                case Left(error) =>
                  HttpResponse(HttpStatus(500, "Internal Server Error"), List.empty, Body(error.message))
                case Right(content) =>
                  HttpResponse(HttpStatus(200, "OK"), List.empty, Body(content))
              }
          }
      }
    }
  }

  def handleHistory(httpRequest: HttpRequest): HttpResponse = {

    val source = Source.fromFile("history.txt")
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

    Files.write(Paths.get("history.txt"), "".getBytes(StandardCharsets.UTF_8))

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
