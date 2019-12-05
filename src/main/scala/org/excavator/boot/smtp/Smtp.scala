package org.excavator.boot.smtp

import java.io.{BufferedReader, InputStreamReader, PrintWriter}
import java.net.Socket
import java.util.concurrent.atomic.AtomicBoolean

import org.slf4j.LoggerFactory


class Smtp {
  val logger = LoggerFactory.getLogger(classOf[Smtp])

  def sendMail(fromAddress:String, rcptAddress: String, content:String, message:String, token: Token) = {

    var socket: Socket = null
    var printWriter:PrintWriter = null
    var bufferReader: BufferedReader = null

    try{
      socket = new Socket(token.address, token.port)

      printWriter = new PrintWriter(socket.getOutputStream, true)

      bufferReader = new BufferedReader(new InputStreamReader(socket.getInputStream))

      //step 1 ehlo cmonkey
      printWriter.println("ehlo cmonkey");
      logger.info(bufferReader.readLine())

      // stop 2 auth
      printWriter.println("auth login")
      logger.info(bufferReader.readLine())

      // username and password
      printWriter.println(token.userName)
      printWriter.println(token.password)

      val loop = new AtomicBoolean(true)
      while(loop.get()){
        val readMsg = bufferReader.readLine()
        logger.info(readMsg)
        if(readMsg.equals("235 Authentication successful")){
          loop.set(false)
        }
      }

      // step 3 set from address
      printWriter.println("mail from:<" + fromAddress)
      logger.info(bufferReader.readLine())

      printWriter.println("rcpt to:<" + rcptAddress)
      logger.info(bufferReader.readLine())

      // step 4 send data
      printWriter.println("data")
      logger.info(bufferReader.readLine())

      // step 5 set content
      printWriter.println("subject:" + content)
      printWriter.println("from:" + fromAddress)
      printWriter.println("to:" + rcptAddress)

      // step 6 set mail format
      printWriter.println("Content-Type: text/plain;charset=\"utf-8\"")
      printWriter.println()

      // step 7 mail subject
      printWriter.println("by java mail")
      printWriter.println(".")
      printWriter.println("")

      logger.info(bufferReader.readLine())

      // step 8 quit
      printWriter.println("rset")
      logger.info(bufferReader.readLine())

      printWriter.println("quit")
      logger.info(bufferReader.readLine())

    }finally {
      bufferReader.close()
      printWriter.close()
      socket.close()
    }
  }
}

object Smtp{
  def apply(): Smtp = new Smtp()
}
