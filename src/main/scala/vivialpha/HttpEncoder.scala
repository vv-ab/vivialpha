package vivialpha

import vivialpha.model.Http.*

object HttpEncoder {

  def encode(response: HttpResponse): Either[EncoderError, String] = {

    val responseLine = s"HTTP/1.1 ${response.status.code} ${response.status.reason}"
    val headerLines = response.headers
      .map({ header => s"${header.fieldName}: ${header.fieldValue}"})
      .mkString("\r\n")
    val body = s"${response.body.content}"

    Right(responseLine + "\r\n" + headerLines + "\r\n\r\n" + body)
  }


  case class EncoderError()
}
