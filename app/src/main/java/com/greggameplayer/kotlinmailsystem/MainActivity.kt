package com.greggameplayer.kotlinmailsystem

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.greggameplayer.kotlinmailsystem.controllers.EmailController

class MainActivity : AppCompatActivity() {
    var emailController : EmailController = EmailController()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        emailController.appExecutors = AppExecutors()
        setContentView(R.layout.activity_main)
    }
}
