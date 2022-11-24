package vivialpha

import java.io.File
import scala.io.Source

object Template {

  def loadTemplate(templateFilePath: String, variables: Map[String, String]): Either[TemplatingError, String] = {

    val templateFile = File(templateFilePath)
    if (templateFile.exists() && templateFile.isFile) {
      val source = Source.fromFile(templateFile)
      val fileContent = source.mkString
      source.close()

      val result = variables.foldLeft(fileContent)({ case (template, (variableName, variableValue)) =>
        template.replace(s"{{$variableName}}", variableValue)
      })
      Right(result)
    }
    else {
      Left(TemplatingError(s"File '$templateFilePath' not found"))
    }
  }

  case class TemplatingError(message: String)
}
