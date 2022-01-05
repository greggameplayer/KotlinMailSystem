package com.greggameplayer.kotlinmailsystem.controllers

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.greggameplayer.kotlinmailsystem.AppExecutors
import com.greggameplayer.kotlinmailsystem.MainActivity
import com.greggameplayer.kotlinmailsystem.ProfileActivity
import com.greggameplayer.kotlinmailsystem.R
import com.greggameplayer.kotlinmailsystem.beans.Credentials
import com.greggameplayer.kotlinmailsystem.beans.MailboxBean
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class SignUp : AppCompatActivity(), CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private lateinit var job: Job
    var retrofitController: RetrofitController = RetrofitController()
    var emailController: EmailController = EmailController()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        job = Job()
        emailController.appExecutors = AppExecutors()
        setContentView(R.layout.inscription)

        val editTextName: EditText = findViewById(R.id.editTextName)
        val editTextEmail: EditText = findViewById(R.id.editTextEmail)
        val editTextPassword: EditText = findViewById(R.id.editTextPassword)
        val editTextPasswordConfirm: EditText = findViewById(R.id.editTextPasswordConfirm)
        val buttonSignUp = findViewById<Button>(R.id.buttonSignUp)
        val intent = Intent(this, MainActivity::class.java)

        buttonSignUp.setOnClickListener {
            launch {
                if (editTextEmail.text.toString() != "" || editTextPassword.text.toString() != ""
                    || editTextName.text.toString() != "" || editTextPasswordConfirm.text.toString() != "" || editTextPassword.text.toString() == editTextPasswordConfirm.text.toString()) {
                    val result = retrofitController.service.createMailbox(
                        MailboxBean(
                            editTextEmail.text.toString(),
                            editTextPassword.text.toString(),
                            editTextName.text.toString(),
                        )
                    )
                    if (result.success == true) {
                        startActivity(intent)
                    } else {
                        Toast.makeText(this@SignUp, "Informations incorrect.", Toast.LENGTH_LONG).show()
                    }
                }
                else{
                    Toast.makeText(this@SignUp, "Veuillez remplir tous les champs avant de valider.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}