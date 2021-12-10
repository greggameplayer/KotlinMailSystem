package com.greggameplayer.kotlinmailsystem

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.greggameplayer.kotlinmailsystem.controllers.EmailController
import com.greggameplayer.kotlinmailsystem.controllers.SendMail


class MainActivity : AppCompatActivity() {
    var emailController : EmailController = EmailController()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        emailController.appExecutors = AppExecutors()
        setContentView(R.layout.activity_main)

        val btPass : Button = findViewById(R.id.bt_pass)

        btPass.setOnClickListener{
            val intent = Intent(this, SendMail::class.java)
            startActivity(intent)
        }

    }

}
