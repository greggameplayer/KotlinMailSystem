package com.greggameplayer.kotlinmailsystem.controllers

import com.greggameplayer.kotlinmailsystem.AppExecutors
import com.greggameplayer.kotlinmailsystem.beans.Credentials
import com.sun.mail.imap.IMAPSSLStore
import java.net.URLEncoder
import java.util.*
import java.util.concurrent.Executors
import javax.activation.DataHandler
import javax.activation.FileDataSource
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMessage
import javax.mail.internet.MimeMultipart


enum class Mailboxes(val value: String) {
    INBOX("INBOX"),
    SENT("Sent"),
    DRAFTS("Drafts"),
    TRASH("Trash")
}

class EmailController {
    lateinit var appExecutors: AppExecutors

    private val props: Properties = System.getProperties()

    init {
        props["mail.smtp.host"] = "mx.gregoire.live"
        props["mail.imaps.host"] = "mx.gregoire.live"
        props["mail.smtp.socketFactory.port"] = "587"
        props["mail.smtp.socketFactory.class"] =
            "com.greggameplayer.kotlinmailsystem.controllers.AlwaysTrustSSLContextFactory"
        props["mail.imaps.socketFactory.port"] = "993"
        props["mail.imaps.socketFactory.class"] =
            "com.greggameplayer.kotlinmailsystem.controllers.AlwaysTrustSSLContextFactory"
        props["mail.smtp.auth"] = "true"
        props["mail.imaps.auth"] = "true"
        props["mail.smtp.port"] = "587"
        props["mail.imaps.port"] = "993"
        props["mail.smtp.ssl.trust"] = "*"
        props["mail.imaps.ssl.trust"] = "*"
        props["mail.smtp.starttls.enable"] = "true"
        props["mail.imaps.starttls.enable"] = "true"
    }

    fun sendEmail(email: String, subject: String, content: String, attachment: String = "") {
        println("Sending email to $email with subject $subject and content $content ${if (attachment.isNotEmpty()) " and attachment $attachment" else ""}")
        appExecutors.diskIO().execute {
            val session = Session.getDefaultInstance(props,
                object : Authenticator() {
                    //Authenticating the password
                    override fun getPasswordAuthentication(): PasswordAuthentication {
                        return PasswordAuthentication(Credentials.EMAIL, Credentials.PASSWORD)
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

                appExecutors.networkIO().execute {
                    Transport.send(message)
                }

                appExecutors.networkIO().execute {
                    val store = IMAPSSLStore(session, URLName("imaps://${URLEncoder.encode(Credentials.EMAIL, "UTF-8")}:${Credentials.PASSWORD}@mx.gregoire.live:993"))
                    store.connect()
                    val folder = store.getFolder("Sent")
                    folder.open(Folder.READ_WRITE)
                    folder.appendMessages(arrayOf(message))
                    message.setFlag(Flags.Flag.RECENT, true)
                    message.setFlag(Flags.Flag.SEEN, true)
                    folder.close()
                    store.close()
                }

                appExecutors.mainThread().execute {
                    println("Email sent successfully and saved to Sent folder")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun retrieveAllEmails(mailboxes: Mailboxes, callback: (Array<Message>) -> Unit) {
        var messages: Array<Message>
        val networkThread = appExecutors.networkIO()
        val mainThread = appExecutors.mainThread()
        val diskThread = appExecutors.diskIO()

        diskThread.execute {
            val session = Session.getDefaultInstance(props,
                object : Authenticator() {
                    //Authenticating the password
                    override fun getPasswordAuthentication(): PasswordAuthentication {
                        return PasswordAuthentication(Credentials.EMAIL, Credentials.PASSWORD)
                    }
                })

            try {

                networkThread.execute {
                    val store = IMAPSSLStore(session, URLName("imaps://${URLEncoder.encode(Credentials.EMAIL, "UTF-8")}:${Credentials.PASSWORD}@mx.gregoire.live:993"))
                    store.connect()
                    val folder = store.getFolder(mailboxes.value)
                    folder.open(Folder.READ_ONLY)
                    messages = folder.messages
                    folder.close()
                    store.close()
                    mainThread.execute {
                        println("Retrieved ${messages.size} emails")
                    }
                    callback.invoke(messages)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}
