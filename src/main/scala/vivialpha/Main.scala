package vivialpha

import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.{SelectionKey, Selector, ServerSocketChannel, SocketChannel}
import java.nio.charset.StandardCharsets
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
  client.read(buffer)
  buffer.flip()
  val request = String(buffer.array(), StandardCharsets.UTF_8).trim
  buffer.clear()
  HttpDecoder.decode(request) match {
    case Right(httpRequest) =>
      println(s"received request: $httpRequest")
      val httpResponse = HttpResponse(HttpStatus(200, "OK"), List.empty, Body("Hello World"))
      HttpEncoder.encode(httpResponse) match {
        case Left(value) => ???
        case Right(response) =>
          println(s"sending response: $response")
          buffer.put(response.getBytes(StandardCharsets.UTF_8))
          buffer.flip()
          client.write(buffer)
      }
    case Left(value) => ???
  }
  buffer.clear()
  client.close()
}
