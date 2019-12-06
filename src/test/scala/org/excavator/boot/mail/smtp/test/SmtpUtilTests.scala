package org.excavator.boot.mail.smtp.test

import org.excavator.boot.mail.smtp.{SmtpUtil, Token}
import org.junit.jupiter.api.{DisplayName, Test}

class SmtpUtilTests {

  @Test
  @DisplayName("test send mail")
  def testSendMail(): Unit = {
    val smtpAddress = System.getProperty("smtpAddress", "smtp.163.com")
    val smtpPassword = System.getProperty("smtpPassword", "")
    val fromAddress = System.getProperty("fromAddress", "example@mail.com")
    val rcptAddress = System.getProperty("rcptAddress", "example@mail.com")
    val subject = System.getProperty("subject", "test subject")
    val content = System.getProperty("content", "test content")

    val token = Token(smtpAddress, 25, fromAddress, smtpPassword)
    SmtpUtil().sendMail(fromAddress, rcptAddress, subject, content, token)
  }
}
