package com.greggameplayer.kotlinmailsystem

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.greggameplayer.kotlinmailsystem.beans.Credentials
import com.greggameplayer.kotlinmailsystem.beans.MailboxBean
import com.greggameplayer.kotlinmailsystem.controllers.EmailController
import com.greggameplayer.kotlinmailsystem.controllers.RetrofitController
import com.greggameplayer.kotlinmailsystem.controllers.SendMail
import com.greggameplayer.kotlinmailsystem.controllers.SignUp
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext


class MainActivity : AppCompatActivity(), CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private lateinit var job: Job
    var emailController: EmailController = EmailController()
    var retrofitController: RetrofitController = RetrofitController()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        job = Job()
        emailController.appExecutors = AppExecutors()
        setContentView(R.layout.connexion)

        val etEmail: EditText = findViewById(R.id.etEmail)
        val etPassword: EditText = findViewById(R.id.etPassword)

        val btLogin = findViewById<Button>(R.id.btLogin)
        val btSignup = findViewById<Button>(R.id.btSignup)

        btLogin.setOnClickListener {
            launch {
                if (etEmail.text.toString() != "" || etPassword.text.toString() != "") {
                    val result = retrofitController.service.verifyMailbox(
                        MailboxBean(
                            etEmail.text.toString(),
                            etPassword.text.toString()
                        )
                    )
                    if (result.success == true) {
                        Credentials.EMAIL = result.username ?: ""
                        Credentials.PASSWORD = result.password ?: ""
                        Credentials.NAME = result.name ?: ""
                        val intent = Intent(this@MainActivity, ProfileActivity::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this@MainActivity, "Identifiant incorrect.", Toast.LENGTH_LONG).show()
                    }
                }
                else{
                    Toast.makeText(this@MainActivity, "Veuillez remplir tous les champs avant de valider.", Toast.LENGTH_LONG).show()
                }
            }
        }

        btSignup.setOnClickListener {
            val intent = Intent(this, SignUp::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroy() {
        job.cancel() // cancel the Job
        super.onDestroy()
    }
}
