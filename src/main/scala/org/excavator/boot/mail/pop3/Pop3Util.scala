package org.excavator.boot.mail.pop3

import java.io.{BufferedReader, InputStreamReader, PrintWriter}
import java.net.Socket
import java.util.StringTokenizer
import java.util.concurrent.atomic.AtomicBoolean

import org.slf4j.LoggerFactory

class Pop3Util {
  val logger = LoggerFactory.getLogger(classOf[Pop3Util])

  def retriverMail(pop3Address: String, port: Int, userName:String, password: String) = {

    var socket: Socket = null
    var printWriter: PrintWriter = null
    var bufferedReader: BufferedReader = null

    try{

      socket = new Socket(pop3Address, port)
      printWriter = new PrintWriter(socket.getOutputStream, true)
      bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream))

      // step 1 auth login
      printWriter.println("user " + userName)
      printWriter.println("pass " + password)

      logger.info("auth login [{}]", bufferedReader.readLine())

      // step 2 stat
      printWriter.println("STAT")
      logger.info("stat info = [{}]", bufferedReader.readLine())

      printWriter.println("list")

      val loop = new AtomicBoolean(true)
      while(loop.get()){
        val elem = bufferedReader.readLine()
        if(null != elem){
          if(!"+OK".equals(elem.substring(0, 3))) {
            val stringTokenizer = new StringTokenizer(elem)
            val num = Integer.parseInt(stringTokenizer.nextToken())
            val messageSize = Integer.parseInt(stringTokenizer.nextToken())
            logger.info("list info = [{}] by num = [{}] messageSize = [{}]", Array(elem, num, messageSize))
          }else{
            logger.info("list info = [{}]", elem)
          }
        }else if (".".equals(elem)){
          loop.set(false)
        }else{
          loop.set(false)
        }
      }

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
