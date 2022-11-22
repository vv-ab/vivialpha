package vivialpha

import java.io.File
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.{SelectionKey, Selector, ServerSocketChannel, SocketChannel}
import java.nio.charset.StandardCharsets
import scala.io.Source
import scala.jdk.CollectionConverters.*

case class HttpRequest(method: Method, uri: URI, headers: List[Header], body: Option[Body])

case class HttpResponse(status: HttpStatus, headers: List[Header], body: Body)

case class HttpStatus(code: Int, reason: String)

enum Method {
  case OPTIONS, GET, HEAD, POST, PUT, DELETE, TRACE, CONNECT
}

case class Header(fieldName: String, fieldValue: String)

case class Body(content: String)

case class URI(value: String)

@main
def server(): Unit = {

  val selector = Selector.open()

  val serverSocket = ServerSocketChannel.open()
  serverSocket.bind(InetSocketAddress("0.0.0.0", 1234))
  serverSocket.configureBlocking(false)
  serverSocket.register(selector, SelectionKey.OP_ACCEPT)

  val buffer = ByteBuffer.allocate(4096)

  while (true) {
    selector.select()
    val selectedKeys = selector.selectedKeys()
    for (key <- selectedKeys.asScala) {
      if (key.isAcceptable) {
        register(selector, serverSocket)
      }
      if (key.isReadable) {
        handle(buffer, key)
      }
      selectedKeys.remove(key)
    }
  }
}

def register(selector: Selector, serverSocket: ServerSocketChannel): Unit = {
  println("registering new client")
  val client = serverSocket.accept()
  client.configureBlocking(false)
  client.register(selector, SelectionKey.OP_READ)
  println("new client registered")
}

def handle(buffer: ByteBuffer, key: SelectionKey): Unit = {
  val client = key.channel().asInstanceOf[SocketChannel]
  buffer.clear()
  client.read(buffer)
  buffer.flip()
  val request = String(buffer.array(), 0, buffer.limit(), StandardCharsets.UTF_8).trim
  buffer.clear()
  HttpDecoder.decode(request) match {
    case Right(httpRequest) =>
      println(request)
      val response = httpRequest.uri.value match {
        case "/vivi" =>
          HttpResponse(HttpStatus(200, "OK"), List.empty, Body("<h1>Hey I'm Vivi</h1>"))
        case "/hello" =>
          val content = httpRequest.body.get.content
          val responseContent = content.substring(content.indexOf("=") + 1)
          HttpResponse(HttpStatus(200, "OK"), List.empty, Body(s"<h1>Hello $responseContent</h1>"))
        case _ =>
          val webRootDirectory = new File("web/")
          val sourceFile = new File(webRootDirectory, httpRequest.uri.value)
          if (sourceFile.exists() && sourceFile.isFile) {
            val source = Source.fromFile(sourceFile)
            val responseBody = source.mkString
            HttpResponse(HttpStatus(200, "OK"), List.empty, Body(responseBody))
          }
          else {
            HttpResponse(HttpStatus(404, "Not Found"), List.empty, Body(s"File not found: ${sourceFile.getPath}"))
          }
      }
      HttpEncoder.encode(response) match {
        case Left(value) => ???
        case Right(httpResponse) =>
          buffer.put(httpResponse.getBytes(StandardCharsets.UTF_8))
          buffer.flip()
          client.write(buffer)
      }
    case Left(value) => ???
  }
  client.close()
}
