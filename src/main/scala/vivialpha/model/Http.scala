package vivialpha.model

object Http {

  case class HttpRequest(method: Method, uri: URI, headers: List[Header], body: Option[Body])

  case class HttpResponse(status: HttpStatus, headers: List[Header], body: Body)

  case class HttpStatus(code: Int, reason: String)

  enum Method {
    case OPTIONS, GET, HEAD, POST, PUT, DELETE, TRACE, CONNECT
  }

  case class Header(fieldName: String, fieldValue: String)

  case class Body(content: String)

  case class URI(value: String)
}