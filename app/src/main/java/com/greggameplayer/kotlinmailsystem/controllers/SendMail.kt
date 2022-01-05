package com.greggameplayer.kotlinmailsystem.controllers

import android.os.Bundle
import android.text.Editable
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.greggameplayer.kotlinmailsystem.AppExecutors
import com.greggameplayer.kotlinmailsystem.R

class SendMail : AppCompatActivity() {
    var emailController: EmailController = EmailController()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        emailController.appExecutors = AppExecutors()
        setContentView(R.layout.send_mail)

        //rrecovery of the "to" variable in the case of a reply to an e-mail
        val to = intent.extras?.get("to")

        //Button
        val btClose : ImageButton = findViewById(R.id.bt_close)
        val btSend : Button = findViewById(R.id.bt_send)

        // EditText
        val etTo : EditText = findViewById(R.id.et_to)
        val etSubject : EditText = findViewById(R.id.et_subject)
        val etContent : EditText = findViewById(R.id.et_content)

        //set the variable previously retrieved in the editText "etTo" in the case of a reply to an email
        to?.let {
            etTo.text = Editable.Factory.getInstance().newEditable(to.toString())
        }

        //backwards
        btClose.setOnClickListener{
            this.finish()
        }

        btSend.setOnClickListener{
            //call the send mail service
            emailController.sendEmail(etTo.text.toString(), etSubject.text.toString(), etContent.text.toString())
            this.finish()
            //user feedback
            Toast.makeText(this, "Le mail a bien été envoyé.", Toast.LENGTH_LONG).show()
        }



    }
}
