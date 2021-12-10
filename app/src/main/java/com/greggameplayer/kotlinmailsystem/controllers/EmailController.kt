package com.greggameplayer.kotlinmailsystem.controllers

import com.greggameplayer.kotlinmailsystem.AppExecutors
import com.greggameplayer.kotlinmailsystem.beans.Credentials
import javax.activation.DataHandler
import javax.activation.FileDataSource
import javax.mail.Message
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMessage
import javax.mail.internet.MimeMultipart

class EmailController {
    lateinit var appExecutors: AppExecutors

    fun sendEmail(email: String, subject: String, content: String, attachment: String = "") {
        println("Sending email to $email with subject $subject and content $content ${if (attachment.isNotEmpty()) " and attachment $attachment" else ""}")
        appExecutors.diskIO().execute {
            val props = System.getProperties()
            props["mail.smtp.host"] = "mx.gregoire.live"
            props["mail.smtp.socketFactory.port"] = "587"
            props["mail.smtp.socketFactory.class"] = "javax.net.ssl.SSLSocketFactory"
            props["mail.smtp.auth"] = "true"
            props["mail.smtp.port"] = "587"
            props["mail.smtp.ssl.trust"] = "*"
            props["mail.smtp.debug"] = "true"

            val session = javax.mail.Session.getDefaultInstance(props,
                object : javax.mail.Authenticator() {
                    //Authenticating the password
                    override fun getPasswordAuthentication(): javax.mail.PasswordAuthentication {
                        return javax.mail.PasswordAuthentication(Credentials.EMAIL, Credentials.PASSWORD)
                    }
        })

            try {
                val message = MimeMessage(session)
                message.setFrom(InternetAddress(Credentials.EMAIL))
                message.setRecipients(Message.RecipientType.TO, email)
                message.subject = subject
                message.setText(content)

                if (attachment.isNotEmpty()) {
                    val attachmentFile = FileDataSource(attachment)
                    val attachmentPart = MimeBodyPart()
                    attachmentPart.dataHandler = DataHandler(attachmentFile)
                    attachmentPart.fileName = attachmentFile.name
                    val multipart = MimeMultipart()
                    multipart.addBodyPart(attachmentPart)
                    message.setContent(multipart)
                }

                Transport.send(message)

                appExecutors.mainThread().execute {
                    println("Email sent successfully")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun retrieveAllEmails() {
        println("Retrieving all emails")
    }

}
