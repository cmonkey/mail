package org.excavator.boot.smtp

import java.util.Base64

import scala.beans.BeanProperty

case class Token(@BeanProperty address:String,
                 @BeanProperty port:Int,
                 @BeanProperty var userName: String,
                 @BeanProperty var password: String){
  userName = Base64.getEncoder.encodeToString(userName.getBytes)
  password = Base64.getEncoder.encodeToString(password.getBytes())
}
