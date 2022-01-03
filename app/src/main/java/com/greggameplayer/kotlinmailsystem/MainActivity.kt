package com.greggameplayer.kotlinmailsystem

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.greggameplayer.kotlinmailsystem.beans.MailboxBean
import com.greggameplayer.kotlinmailsystem.beans.RetrofitResponse
import com.greggameplayer.kotlinmailsystem.controllers.EmailController
import com.greggameplayer.kotlinmailsystem.controllers.RetrofitController
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
        val btLogin = findViewById<Button>(R.id.buttonConnexion)
        val btSignup = findViewById<Button>(R.id.buttonInscription)

        btLogin.setOnClickListener {
            launch {
                val result = retrofitController.service.verifyMailbox(MailboxBean("kotlin@gregoire.live", "kotlin"))
                println(result.success ?: "")
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
