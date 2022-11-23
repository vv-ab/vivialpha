package vivialpha

object HttpDecoder {

  def decode(request: String): Either[DecodeError, HttpRequest] = {

    val lines: List[String] = request.split("\r\n").toList
    val startLine = lines.head
    val startLines = startLine.split(" ")
    val methodAndUri = startLines match {
      case Array("POST", uri, _) =>
        Right((Method.POST, URI(uri)))
      case Array("GET", uri, _) =>
        Right((Method.GET, URI(uri)))
      case Array("OPTIONS", uri, _) =>
        Right((Method.OPTIONS, URI(uri)))
      case Array("HEAD", uri, _) =>
        Right((Method.HEAD, URI(uri)))
      case Array("DELETE", uri, _) =>
        Right((Method.DELETE, URI(uri)))
      case Array("TRACE", uri, _) =>
        Right((Method.TRACE, URI(uri)))
      case Array("PUT", uri, _) =>
        Right((Method.PUT, URI(uri)))
      case Array("CONNECT", uri, _) =>
        Right((Method.CONNECT, URI(uri)))
      case unknown =>
        Left(DecodeError(s"Failed to parse http method: ${unknown.mkString}"))
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
    methodAndUri.map({ case (method, uri) =>
      HttpRequest(method, uri, headers, body)
    })
  }

  case class DecodeError(message: String)
}
