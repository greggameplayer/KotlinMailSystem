package com.greggameplayer.kotlinmailsystem.beans

// Bean class representing an email
class Email {
    var id: Int = 0
    var from: String = ""
    var to: String = ""
    var subject: String = ""
    var body: String = ""
    var date: String = ""
    var read: Boolean = false

    override fun toString(): String {
        return "Email(id=$id, from='$from', to='$to', subject='$subject', body='$body', date='$date', read=$read)"
    }
}
