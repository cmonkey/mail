package org.excavator.boot.mail.pop3

import java.io.{BufferedInputStream, BufferedReader, PrintWriter}
import java.net.Socket

import org.slf4j.LoggerFactory

class Pop3Util {
  val logger = LoggerFactory.getLogger(classOf[Pop3Util])

  def retriverMail() = {

    var socket: Socket = null
    var printWriter: PrintWriter = null
    var bufferedReader: BufferedReader = null

    try{

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
