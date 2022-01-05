package com.greggameplayer.kotlinmailsystem.controllers

import com.greggameplayer.kotlinmailsystem.AppExecutors
import com.greggameplayer.kotlinmailsystem.beans.Credentials
import com.greggameplayer.kotlinmailsystem.beans.PaginatedEmails
import com.greggameplayer.kotlinmailsystem.enums.Mailboxes
import com.sun.mail.imap.IMAPSSLStore
import java.io.IOException
import java.net.URLEncoder
import java.util.*
import javax.activation.DataHandler
import javax.activation.FileDataSource
import javax.inject.Singleton
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMessage
import javax.mail.internet.MimeMultipart
import kotlin.math.ceil


@Singleton
class EmailController {
    lateinit var appExecutors: AppExecutors

    private val props: Properties = System.getProperties()

    private val session: Session

    init {
        props["mail.smtp.host"] = "mx.gregoire.live"
        props["mail.imaps.host"] = "mx.gregoire.live"
        props["mail.smtp.socketFactory.port"] = "587"
        props["mail.smtp.socketFactory.class"] =
            "com.greggameplayer.kotlinmailsystem.beans.AlwaysTrustSSLContextFactory"
        props["mail.imaps.socketFactory.port"] = "993"
        props["mail.imaps.socketFactory.class"] =
            "com.greggameplayer.kotlinmailsystem.beans.AlwaysTrustSSLContextFactory"
        props["mail.smtp.auth"] = "true"
        props["mail.imaps.auth"] = "true"
        props["mail.smtp.port"] = "587"
        props["mail.imaps.port"] = "993"
        props["mail.smtp.ssl.trust"] = "*"
        props["mail.imaps.ssl.trust"] = "*"
        props["mail.smtp.starttls.enable"] = "true"
        props["mail.imaps.starttls.enable"] = "true"

        session = Session.getDefaultInstance(props,
            object : Authenticator() {
                //Authenticating the password
                override fun getPasswordAuthentication(): PasswordAuthentication {
                    return PasswordAuthentication(Credentials.EMAIL, Credentials.PASSWORD)
                }
            })
    }

    fun sendEmail(email: String, subject: String, content: String, attachment: String = "") {
        println("Sending email to $email with subject $subject and content $content ${if (attachment.isNotEmpty()) " and attachment $attachment" else ""}")
        appExecutors.diskIO().execute {
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
                    val store = IMAPSSLStore(
                        session,
                        URLName(
                            "imaps://${
                                URLEncoder.encode(
                                    Credentials.EMAIL,
                                    "UTF-8"
                                )
                            }:${Credentials.PASSWORD}@mx.gregoire.live:993"
                        )
                    )
                    store.connect()
                    val folder = store.getFolder(Mailboxes.SENT.value)
                    folder.open(Folder.READ_WRITE)
                    folder.appendMessages(arrayOf(message))
                    message.setFlag(Flags.Flag.RECENT, true)
                    message.setFlag(Flags.Flag.SEEN, true)
                    message.saveChanges()
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

    fun retrieveAllEmails(mailbox: Mailboxes, callback: (Array<Message>) -> Unit) {
        var messages: Array<Message>
        val networkThread = appExecutors.networkIO()
        val mainThread = appExecutors.mainThread()
        val diskThread = appExecutors.diskIO()

        diskThread.execute {
            try {

                networkThread.execute {
                    val store = IMAPSSLStore(
                        session,
                        URLName(
                            "imaps://${
                                URLEncoder.encode(
                                    Credentials.EMAIL,
                                    "UTF-8"
                                )
                            }:${Credentials.PASSWORD}@mx.gregoire.live:993"
                        )
                    )
                    store.connect()
                    val folder = store.getFolder(mailbox.value)
                    folder.open(Folder.READ_ONLY)
                    messages = folder.messages
                    mainThread.execute {
                        println("Retrieved ${messages.size} emails")
                    }
                    callback.invoke(messages)
                    folder.close()
                    store.close()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun retrievePaginatedEmails(
        mailbox: Mailboxes,
        page: Int,
        itemsPerPage: Int = 10,
        callback: (PaginatedEmails) -> Unit
    ) {
        var messages: Array<Message>
        var firstMessageOnRequestedPage: Int
        var lastMessageOnRequestedPage: Int
        val networkThread = appExecutors.networkIO()
        val mainThread = appExecutors.mainThread()
        val diskThread = appExecutors.diskIO()

        diskThread.execute {
            try {
                networkThread.execute {
                    val store = IMAPSSLStore(
                        session,
                        URLName(
                            "imaps://${
                                URLEncoder.encode(
                                    Credentials.EMAIL,
                                    "UTF-8"
                                )
                            }:${Credentials.PASSWORD}@mx.gregoire.live:993"
                        )
                    )
                    store.connect()
                    val folder = store.getFolder(mailbox.value)
                    folder.open(Folder.READ_ONLY)
                    val totalPages = ceil(folder.messageCount / itemsPerPage.toDouble()).toInt()
                    val paginatedEmails =
                        PaginatedEmails(page = page + 1, totalPages = totalPages, itemsPerPage = itemsPerPage)
                    firstMessageOnRequestedPage = page * itemsPerPage + 1
                    lastMessageOnRequestedPage = firstMessageOnRequestedPage + itemsPerPage - 1
                    messages = if (firstMessageOnRequestedPage > folder.messageCount) {
                        arrayOf()
                    } else {
                        if (lastMessageOnRequestedPage > folder.messageCount) {
                            folder.getMessages(firstMessageOnRequestedPage, folder.messageCount).reversedArray()
                        } else {
                            folder.getMessages(firstMessageOnRequestedPage, lastMessageOnRequestedPage).reversedArray()
                        }
                    }
                    paginatedEmails.emails = messages
                    folder.close()
                    store.close()
                    mainThread.execute {
                        println("Retrieved ${messages.size} emails")
                    }
                    callback.invoke(paginatedEmails)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getEmailsCount(mailbox: Mailboxes, notSeen: Boolean = false, callback: (Int) -> Unit) {
        appExecutors.diskIO().execute {
            try {
                appExecutors.networkIO().execute {
                    val store = IMAPSSLStore(
                        session,
                        URLName(
                            "imaps://${
                                URLEncoder.encode(
                                    Credentials.EMAIL,
                                    "UTF-8"
                                )
                            }:${Credentials.PASSWORD}@mx.gregoire.live:993"
                        )
                    )
                    store.connect()
                    val folder = store.getFolder(mailbox.value)
                    folder.open(Folder.READ_ONLY)
                    if(notSeen) {
                        callback.invoke(folder.unreadMessageCount)
                    } else {
                        callback.invoke(folder.messageCount)
                    }
                    folder.close()
                    store.close()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun setSeen(mailbox: Mailboxes, message: Message, status: Boolean, callback: ((Boolean) -> Unit)?) {
        val mainThread = appExecutors.mainThread()

        appExecutors.diskIO().execute {
            try {
                appExecutors.networkIO().execute {
                    val store = IMAPSSLStore(
                        session,
                        URLName(
                            "imaps://${
                                URLEncoder.encode(
                                    Credentials.EMAIL,
                                    "UTF-8"
                                )
                            }:${Credentials.PASSWORD}@mx.gregoire.live:993"
                        )
                    )
                    store.connect()
                    val folder = store.getFolder(mailbox.value)
                    folder.open(Folder.READ_WRITE)
                    val modifiedMessage = folder.getMessage(message.messageNumber)
                    modifiedMessage.setFlag(Flags.Flag.SEEN, status)
                    modifiedMessage.saveChanges()
                    folder.close()
                    store.close()
                    mainThread.execute {
                        callback?.invoke(true)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                mainThread.execute {
                    callback?.invoke(false)
                }
            }
        }
    }


    @Throws(MessagingException::class, IOException::class)
    public fun getTextFromMessage(message: Message): String? {
        var result = ""
        if (message.isMimeType("text/plain")) {
            result = message.content.toString()
        } else if (message.isMimeType("text/html")) { // **
            result = message.content.toString() // **
        } else if (message.isMimeType("multipart/*")) {
            val mimeMultipart = message.content as MimeMultipart
            result = getTextFromMimeMultipart(mimeMultipart)
        }
        return result
    }

    @Throws(MessagingException::class, IOException::class)
    public fun getTextFromMimeMultipart(
        mimeMultipart: MimeMultipart
    ): String {
        var result = ""
        val count = mimeMultipart.count
        for (i in 0 until count) {
            val bodyPart = mimeMultipart.getBodyPart(i)
            if (bodyPart.isMimeType("text/plain")) {
                result = """
                $result
                ${bodyPart.content}
                """.trimIndent()
                break // without break same text appears twice in my tests
            } else if (bodyPart.isMimeType("text/html")) {
                val html = bodyPart.content as String
                result = """
                $result
                ${org.jsoup.Jsoup.parse(html).text()}
                """.trimIndent()
            } else if (bodyPart.content is MimeMultipart) {
                result = result + getTextFromMimeMultipart(bodyPart.content as MimeMultipart)
            }
        }
        return result
    }
}
