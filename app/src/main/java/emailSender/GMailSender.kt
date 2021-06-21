package emailSender

import android.util.Log
import java.security.Security
import java.util.*
import javax.activation.DataHandler
import javax.activation.DataSource
import javax.activation.FileDataSource
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMessage
import javax.mail.internet.MimeMultipart


class GMailSender(user: String?, password: String?): Authenticator() {

    private val GMailhost = "smtp.gmail.com"
    private val GoDaddyhost = "smtpout.secureserver.net"
    private var user: String? = null
    private var password: String? = null
    private var session: Session? = null


    init {
        Security.addProvider(JSSEProvider())
    }

    init {
        this.user = user
        this.password = password
        val props = Properties()

        props.setProperty("mail.transport.protocol", "smtp")
        props.setProperty("mail.host", GMailhost)
        props.put("mail.smtp.auth", "true")
        props.put("mail.smtp.port", "465")
        props.put("mail.smtp.socketFactory.port", "465")
        props.put(
                "mail.smtp.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory"
        )
        props.put("mail.smtp.socketFactory.fallback", "false")
        props.setProperty("mail.smtp.quitwait", "false")

/* props.put("mail.host", GoDaddyhost);
 props.put("mail.smtp.auth", "true");
 props.put("mail.smtp.ssl.enable", "true");*/




 session = Session.getDefaultInstance(props, this)
}



override fun getPasswordAuthentication(): PasswordAuthentication {
 return PasswordAuthentication(user, password)
}





    @Synchronized
    @Throws(Exception::class)
    fun sendMailToClient(filename: String?, subject: String?, body: String, sender: String?, toEmail: String) {

        try {
            val message = MimeMessage(session)


            message.sender = InternetAddress(sender)

            //message.setFrom(InternetAddress(sender))

            /* for (toEmail in toEmailList) {
                 message.addRecipient(Message.RecipientType.TO,
                         InternetAddress(toEmail))
             }*/


            message.addRecipient(Message.RecipientType.TO,
                    InternetAddress(toEmail))


            message.subject = subject

            var messageBodyPart: BodyPart = MimeBodyPart()

            messageBodyPart.setText(body)

            val multipart: Multipart = MimeMultipart()
            multipart.addBodyPart(messageBodyPart)
            messageBodyPart = MimeBodyPart()

            val source: DataSource = FileDataSource(filename)
            messageBodyPart.setDataHandler(DataHandler(source))
            messageBodyPart.setFileName(filename)
            multipart.addBodyPart(messageBodyPart)

            message.setContent(multipart)

            Transport.send(message)


        } catch (e: java.lang.Exception) {

            Log.e("SendMail", e.message, e)
        }
    }



    @Synchronized
    @Throws(Exception::class)
    fun sendMailToOffice(file: String?,fileWithPrice: String?, subject: String?, body: String, sender: String?, toEmail: String) {

        try {
            val message = MimeMessage(session)


            message.sender = InternetAddress(sender)

            //message.setFrom(InternetAddress(sender))

            /* for (toEmail in toEmailList) {
                 message.addRecipient(Message.RecipientType.TO,
                         InternetAddress(toEmail))
             }*/


            message.addRecipient(Message.RecipientType.TO,
                    InternetAddress(toEmail))


            message.subject = subject



            val multipart: Multipart = MimeMultipart()


            var messageBodyPart: BodyPart = MimeBodyPart()
            messageBodyPart.setText(body)
            multipart.addBodyPart(messageBodyPart)

            messageBodyPart = MimeBodyPart()
            val source1: DataSource = FileDataSource(file)
            messageBodyPart.setDataHandler(DataHandler(source1))
            messageBodyPart.setFileName(file)
            multipart.addBodyPart(messageBodyPart)


            messageBodyPart = MimeBodyPart()
            val source2: DataSource = FileDataSource(fileWithPrice)
            messageBodyPart.setDataHandler(DataHandler(source2))
            messageBodyPart.setFileName(fileWithPrice)
            multipart.addBodyPart(messageBodyPart)



            message.setContent(multipart)
            Transport.send(message)


        } catch (e: java.lang.Exception) {

            Log.e("SendMail", e.message, e)
        }
    }



}