package org.excavator.boot.smtp.test

import org.excavator.boot.smtp.{Smtp, Token}
import org.junit.jupiter.api.{DisplayName, Test}

class SmtpTests {

  @Test
  @DisplayName("test send mail")
  def testSendMail(): Unit = {
    val token = Token("smtp.163.com", 25, "", "")
    val fromAddress = ""
    val rcptAddress = ""
    val content = ""
    val message = ""
    Smtp().sendMail(fromAddress, rcptAddress, content, message, token)
  }
}
