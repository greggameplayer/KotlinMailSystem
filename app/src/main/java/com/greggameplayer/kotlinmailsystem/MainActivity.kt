package com.greggameplayer.kotlinmailsystem

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.greggameplayer.kotlinmailsystem.controllers.EmailController
import com.greggameplayer.kotlinmailsystem.controllers.SignUp


class MainActivity : AppCompatActivity() {
    var emailController: EmailController = EmailController()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        emailController.appExecutors = AppExecutors()
        setContentView(R.layout.connexion)
        val btLogin = findViewById<Button>(R.id.buttonConnexion)
        val btSignup = findViewById<Button>(R.id.buttonInscription)

        btLogin.setOnClickListener {
            //
        }

        btSignup.setOnClickListener {
            val intent = Intent(this, SignUp::class.java)
            startActivity(intent)
        }
    }

}
