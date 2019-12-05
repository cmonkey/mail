## 调用

对SmtpUtil().sendMail 调用需要5个参数

1. fromAddress　发件人地址
2. rcptAddress 收件人地址
3. subject  邮件标题
4. content 邮件正文
5. token 对象　

## token object 

1. address smtp 服务器地址
2. port smtp 服务器端口, 一般为25
3. userName smtp 服务器的用户名
4. password smtp 服务器的密码　

## 测试

通过设置环境变量，对163邮件的smtp 发送邮件测试通过 by 2019年12月5日17:50:08
