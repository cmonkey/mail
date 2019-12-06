package org.excavator.boot.mail.pop3

import java.io.{BufferedReader, InputStreamReader, PrintWriter}
import java.net.Socket
import java.nio.charset.{Charset, StandardCharsets}
import java.util
import java.util.{Base64, StringTokenizer}
import java.util.concurrent.atomic.AtomicBoolean
import java.util.function.Consumer

import com.google.common.collect.Lists
import org.apache.commons.collections4.CollectionUtils
import org.apache.commons.lang3.StringUtils
import org.excavator.boot.mail.entity.MailElemnt
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

      val mailElements: java.util.ArrayList[MailElemnt] = getMailElements(bufferedReader)

      if(CollectionUtils.isNotEmpty(mailElements)){
        mailElements.forEach(mailElement => {
          val num = mailElement.num
          val messageSize = mailElement.messageSize
          val subjectConsumer:Consumer[BufferedReader] = (bufferedReader:BufferedReader) => {
            val loop = new AtomicBoolean(true)
            while(loop.get) {
              val subject = bufferedReader.readLine()
              if(StringUtils.isNotBlank(subject)){
                if(!".".equals(subject)) {
                  val splitSubject = subject.split(":")
                  if(splitSubject.head.equals("Subject")) {
                    val tail = splitSubject.tail.head
                    val matchSubjectHeadTupl3 = matchSubjectHead(tail)
                    if(matchSubjectHeadTupl3._1 && tail.endsWith("?=")){
                      var subjectClean = tail.substring(matchSubjectHeadTupl3._3, tail.length)
                      subjectClean = subjectClean.substring(0, subjectClean.length - 2)
                      if(Charset.forName("gb18030").equals(matchSubjectHeadTupl3._2)){
                        logger.info("top subject  num = [{}], subject = [{}]", num, new String(subjectClean.getBytes(matchSubjectHeadTupl3._2)))
                      }else {
                        val bytes = Base64.getDecoder.decode(subjectClean)
                        val parseSubject = new String(bytes, matchSubjectHeadTupl3._2)
                        logger.info("top subject num = [{}], subject = [{}]", num, parseSubject)
                      }
                    }else{
                      logger.info("top subject num = [{}], subject = [{}]", num, tail)
                    }
                  }else{
                    logger.info("top subject num = [{}], subject = [{}]", num, subject)
                  }
                }
              }else{
                loop.set(false)
              }
            }
          }
          // parse mail subject
          getSubject(printWriter, bufferedReader, num , subjectConsumer)

          val contentConsumer: Consumer[BufferedReader] = (bufferedReader) => {
            val loop = new AtomicBoolean(true)
            while(loop.get()){
              val contentLine = bufferedReader.readLine()
              if(StringUtils.isNotBlank(contentLine)){
                logger.info("content by num [{}] line = [{}]", num, contentLine)
              }else{
                loop.set(false)
              }
            }
          }
          // parse mail content
          getContent(printWriter, bufferedReader, num, contentConsumer)
        })
      }

    }finally{
      bufferedReader.close()
      printWriter.close()
      socket.close()
    }
  }

  private def matchSubjectHead(subject: String):Tuple3[Boolean, Charset, Int] = {
    var tuple3: Tuple3[Boolean, Charset, Int]  = Tuple3(false, StandardCharsets.UTF_8, 11)
    if(subject.startsWith(" =?utf-8?B?"))  {
      tuple3 = Tuple3(true, StandardCharsets.UTF_8, 11)
    }else if(subject.startsWith(" =?UTF-8?B?")){
      tuple3 = Tuple3(true, StandardCharsets.UTF_8, 11)
    }else if(subject.startsWith(" =?GBK?B?")){
      tuple3 = Tuple3(true, Charset.forName("gbk"), 9)
    }else if(subject.startsWith(" =?gb2312?B?")){
      tuple3 = Tuple3(true, Charset.forName("gb2312"), 12)
    }else if (subject.startsWith(" =?GB18030?Q?")){
      tuple3 = Tuple3(true, Charset.forName("gb18030"), 13)
    }
    tuple3
  }

  private def getMailElements(bufferedReader: BufferedReader) = {
    val loop = new AtomicBoolean(true)
    val mailElements = Lists.newArrayList[MailElemnt]()
    while (loop.get()) {
      val elem = bufferedReader.readLine()
      if (null != elem) {
        if (".".equals(elem)) {
          loop.set(false)
        } else {
          if (!"+OK".equals(elem.substring(0, 3))) {
            val stringTokenizer = new StringTokenizer(elem)
            val num = Integer.parseInt(stringTokenizer.nextToken())
            val messageSize = Integer.parseInt(stringTokenizer.nextToken())
            logger.info("list info by num = [{}] messageSize = [{}]", num, messageSize)

            val mailElemnt = MailElemnt(num, messageSize)
            mailElements.add(mailElemnt)
          } else {
            logger.info("list info = [{}]", elem)
          }
        }
      } else {
        loop.set(false)
      }
    }
    mailElements
  }

  def getSubject(printWriter: PrintWriter,bufferedReader: BufferedReader, num: Int, consumer: Consumer[BufferedReader]) = {
    printWriter.println("top " + num + " 0")
    consumer.accept(bufferedReader)
  }

  def getContent(printWriter: PrintWriter, bufferedReader: BufferedReader, num: Int, consumer: Consumer[BufferedReader]) = {
    printWriter.println("retr " + num)
    consumer.accept(bufferedReader)
  }
}

object Pop3Util{
  def apply(): Pop3Util = new Pop3Util()
}
