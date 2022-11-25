package vivialpha

import java.io.{File, FileWriter}
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.{SelectionKey, Selector, ServerSocketChannel, SocketChannel}
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}
import scala.io.Source
import scala.jdk.CollectionConverters.*
import compilersandbox.tokenizer.{Preprocessor, Tokenizer}
import compilersandbox.parser.{Node, Operand, Operator, Parser}
import compilersandbox.makeErrorMessage
import vivialpha.model.Http._

object App {

  def main(args: Array[String]): Unit = {
    server(args(0).toInt)
  }

  @main
  def server(port: Int): Unit = {

    val selector = Selector.open()

    val serverSocket = ServerSocketChannel.open()
    serverSocket.bind(InetSocketAddress("0.0.0.0", port))
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
        case "/result" =>
          HttpRoutes.handleResult(httpRequest)
        case "/history" =>
          HttpRoutes.handleHistory(httpRequest)
        case "/clear" =>
          HttpRoutes.handleClear(httpRequest)
        case _ =>
          HttpRoutes.handleStaticContent(httpRequest)
      }
      HttpEncoder.encode(response) match {
        case Left(error) =>
          println(s"EncodeError: ${error.message}")
        case Right(httpResponse) =>
          buffer.put(httpResponse.getBytes(StandardCharsets.UTF_8))
          buffer.flip()
          client.write(buffer)
      }
    case Left(error) =>
      println(s"DecodeError: ${error.message}")
  }
  client.close()
}
