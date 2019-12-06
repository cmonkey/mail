package org.excavator.boot.mail.pop3.test

import org.excavator.boot.mail.pop3.Pop3Util
import org.junit.jupiter.api.{DisplayName, Test}

class Pop3UtilTests {

  @Test
  @DisplayName("test retriverMail")
  def testRetriverMail() = {
    val pop3Address = "pop.163.com"
    val port = 110
    val userName = ""
    val password = ""
    Pop3Util().retriverMail(pop3Address, port, userName, password)
  }

}
