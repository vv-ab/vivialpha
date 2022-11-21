package vivialpha

object HttpEncoder {

  def encode(response: HttpResponse): Either[EncoderError, String] = {

    val responseLine = s"HTTP/1.1 ${response.status.code} ${response.status.reason}"
    val headerLines = response.headers
      .map({ header => s"${header.fieldName}: ${header.fieldValue}"})
      .mkString("\n")
    val body = s"${response.body.content}"

    Right(responseLine + "\n" + headerLines + "\n\n" + body)
  }


  case class EncoderError()
}
