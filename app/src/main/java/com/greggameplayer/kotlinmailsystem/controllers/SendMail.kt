package com.greggameplayer.kotlinmailsystem.controllers

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.greggameplayer.kotlinmailsystem.AppExecutors
import com.greggameplayer.kotlinmailsystem.R

class SendMail : AppCompatActivity() {
    var emailController: EmailController = EmailController()
    val context = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        emailController.appExecutors = AppExecutors()
        setContentView(R.layout.send_mail)

        //Button
        val btClose : ImageButton = findViewById(R.id.bt_close)
        val btSend : Button = findViewById(R.id.bt_send)

        // EditText
        val etTo : EditText = findViewById(R.id.et_to)
        val etSubject : EditText = findViewById(R.id.et_subject)
        val etContent : EditText = findViewById(R.id.et_content)

        btClose.setOnClickListener{
            this.finish()
        }

        btSend.setOnClickListener{
            emailController.sendEmail(etTo.text.toString(), etSubject.text.toString(), etContent.text.toString())
            /*emailController.retrievePaginatedEmails(Mailboxes.SENT, 0, 10) { paginatedEmails ->
                AppExecutors.MainThreadExecutor().execute {
                    println("Message : ${paginatedEmails.emails.size}")
                    println("Page : ${paginatedEmails.page}")
                    println("Total : ${paginatedEmails.totalPages}")
                    println("Next : ${paginatedEmails.hasNextPage}")
                    println("Previous : ${paginatedEmails.hasPreviousPage}")
                    println("ItemsPerPage : ${paginatedEmails.itemsPerPage}")
                    Toast.makeText(applicationContext, "Messages : ${paginatedEmails.emails.size}", Toast.LENGTH_LONG).show()
                }
            }*/
            /* emailController.getEmailsCount(Mailboxes.SENT, true) {
                AppExecutors.MainThreadExecutor().execute {
                    Toast.makeText(applicationContext, "Messages : $it", Toast.LENGTH_LONG).show()
                }
            } */
            this.finish()
            Toast.makeText(this, "Le mail a bien été envoyé.", Toast.LENGTH_LONG).show()
        }



    }
}
