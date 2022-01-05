package com.greggameplayer.kotlinmailsystem.enums

// Enum class representing the different type of mailboxes in the system
enum class Mailboxes(val value: String) {
    INBOX("INBOX"),
    SENT("Sent"),
    DRAFTS("Drafts"),
    TRASH("Trash")
}
