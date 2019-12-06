package org.excavator.boot.mail.pop3

import java.io.{BufferedInputStream, BufferedReader, InputStreamReader, PrintWriter}
import java.net.Socket

import org.excavator.boot.mail.entity.Token
import org.slf4j.LoggerFactory

class Pop3Util {
  val logger = LoggerFactory.getLogger(classOf[Pop3Util])

  def retriverMail(token: Token) = {

    var socket: Socket = null
    var printWriter: PrintWriter = null
    var bufferedReader: BufferedReader = null

    try{

      socket = new Socket(token.address, token.port)
      printWriter = new PrintWriter(socket.getOutputStream, true)
      bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream))

      printWriter.println("user " + token.userName)
      printWriter.println("pass " + token.password)

      logger.info("auth login [{}]", bufferedReader.readLine())

    }finally{
      bufferedReader.close()
      printWriter.close()
      socket.close()
    }
  }
}

object Pop3Util{
  def apply(): Pop3Util = new Pop3Util()
}
