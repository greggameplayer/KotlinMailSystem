package com.greggameplayer.kotlinmailsystem

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.greggameplayer.kotlinmailsystem.beans.RetrofitResponse
import com.greggameplayer.kotlinmailsystem.controllers.EmailController
import com.greggameplayer.kotlinmailsystem.controllers.RetrofitController
import com.greggameplayer.kotlinmailsystem.controllers.SignUp


class MainActivity : AppCompatActivity() {
    lateinit var appExecutors: AppExecutors
    var emailController: EmailController = EmailController()
    var retrofitController: RetrofitController = RetrofitController()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        emailController.appExecutors = AppExecutors()
        setContentView(R.layout.connexion)
        val btLogin = findViewById<Button>(R.id.buttonConnexion)
        val btSignup = findViewById<Button>(R.id.buttonInscription)

        btLogin.setOnClickListener {
            appExecutors.networkIO().execute {
                retrofitController.service.verifyMailbox("kotlin@gregoire.live", "test").enqueue(object : retrofit2.Callback<RetrofitResponse> {
                    override fun onFailure(call: retrofit2.Call<RetrofitResponse>, t: Throwable) {
                        println("fail")
                    }

                    override fun onResponse(call: retrofit2.Call<RetrofitResponse>, response: retrofit2.Response<RetrofitResponse>) {
                        println("success")
                        println(response.body()?.success ?: "null")
                    }
                })
            }
        }

        btSignup.setOnClickListener {
            val intent = Intent(this, SignUp::class.java)
            startActivity(intent)
        }
    }

}
