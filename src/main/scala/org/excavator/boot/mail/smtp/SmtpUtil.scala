package org.excavator.boot.mail.smtp

import java.io.{BufferedReader, InputStreamReader, PrintWriter}
import java.net.Socket
import java.util.concurrent.atomic.AtomicBoolean

import org.slf4j.LoggerFactory

class SmtpUtil {
  val logger = LoggerFactory.getLogger(classOf[SmtpUtil])

  def sendMail(fromAddress:String, rcptAddress: String, subject: String, content:String, token: Token) = {

    var socket: Socket = null
    var printWriter:PrintWriter = null
    var bufferReader: BufferedReader = null

    try{
      socket = new Socket(token.address, token.port)

      printWriter = new PrintWriter(socket.getOutputStream, true)

      bufferReader = new BufferedReader(new InputStreamReader(socket.getInputStream))

      //step 1 ehlo cmonkey 和smtp 服务器建立连接
      printWriter.println("ehlo cmonkey");
      logger.info("ehlo response msg = [{}]", bufferReader.readLine())

      // stop 2 auth　认证
      printWriter.println("auth login")
      logger.info("auth log command response msg = [{}]", bufferReader.readLine())

      // username and password
      printWriter.println(token.userName)
      printWriter.println(token.password)

      val loop = new AtomicBoolean(true)
      while(loop.get()){
        val readMsg = bufferReader.readLine()
        if(null != readMsg) {
          logger.info("auth login response msg = [{}]", readMsg)
          if (readMsg.equals("235 Authentication successful")) {
            loop.set(false)
          }
        }else{
          loop.set(false)
        }
      }

      // step 3 set from address 设置发送者邮箱
      printWriter.println("mail from:<" + fromAddress+">")
      logger.info("mail from [{}]", bufferReader.readLine())

      // 设置收件人邮箱
      printWriter.println("rcpt to:<" + rcptAddress+">")
      logger.info("rcpt to [{}]", bufferReader.readLine())

      // step 4 send data　一个标识，用于标识后面的消息是邮件内容
      printWriter.println("data")
      logger.info("data = [{}]", bufferReader.readLine())

      // step 5 set content 设置标题
      printWriter.println("subject:" + subject)
      // 设置发件人邮箱
      printWriter.println("from:" + fromAddress)
      // 设置收件人邮箱
      printWriter.println("to:" + rcptAddress)

      // step 6 set mail format
      printWriter.println("Content-Type: text/plain;charset=\"utf-8\"")
      printWriter.println()

      // step 7 mail subject
      printWriter.println(content)
      printWriter.println(".")
      printWriter.println("")

      logger.info("send subject [{}]", bufferReader.readLine())

      // step 8 quit
      printWriter.println("rset")
      logger.info("rset [{}]", bufferReader.readLine())

      printWriter.println("quit")
      logger.info("quit = [{}]", bufferReader.readLine())

    }finally {
      bufferReader.close()
      printWriter.close()
      socket.close()
    }
  }
}

object SmtpUtil{
  def apply(): SmtpUtil = new SmtpUtil
}
