package vivialpha

object HttpDecoder {

  def decode(request: String): Either[DecodeError, HttpRequest] = {

    val lines: List[String] = request.split("\r\n").toList
    val startLine = lines.head
    val startLines = startLine.split(" ")
    val (method, uri) = startLines match {
      case Array("POST", uri, _) =>
        (Method.POST, URI(uri))
      case Array("GET", uri, _) =>
        (Method.GET, URI(uri))
      case Array("OPTIONS", uri, _) =>
        (Method.OPTIONS, URI(uri))
      case Array("HEAD", uri, _) =>
        (Method.HEAD, URI(uri))
      case Array("DELETE", uri, _) =>
        (Method.DELETE, URI(uri))
      case Array("TRACE", uri, _) =>
        (Method.TRACE, URI(uri))
      case Array("PUT", uri, _) =>
        (Method.PUT, URI(uri))
      case Array("CONNECT", uri, _) =>
        (Method.CONNECT, URI(uri))
    }
    val (headerLines, bodyLines) = lines.tail.span({ line => line != "" })
    val headers = headerLines.map({ line =>
      val splits = line.split(": ")
      val fieldName = splits(0)
      val fieldValue = splits(1)
      val header: Header = Header(fieldName, fieldValue)
      header
    })
    val body = if (bodyLines.isEmpty) {
      None
    }
    else {
      Some(Body(bodyLines.tail.mkString))
    }
    Right(HttpRequest(method, uri, headers, body))
  }

  case class DecodeError()
}
