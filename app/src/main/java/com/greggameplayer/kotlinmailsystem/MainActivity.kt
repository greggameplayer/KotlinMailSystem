package com.greggameplayer.kotlinmailsystem

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import com.greggameplayer.kotlinmailsystem.beans.Credentials
import com.greggameplayer.kotlinmailsystem.controllers.*
import com.greggameplayer.kotlinmailsystem.enums.Mailboxes
import kotlinx.android.synthetic.main.drawer_content.*
import kotlinx.android.synthetic.main.test_activity_main.*


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener{//, CoroutineScope {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.test_activity_main)
        setSupportActionBar(toolbar)


        val toggle = ActionBarDrawerToggle(this, test, toolbar, R.string.open, R.string.close)
        toggle.isDrawerIndicatorEnabled = true
        test.addDrawerListener(toggle)
        toggle.syncState()

        test_nav_menu.setNavigationItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        if(item.itemId == R.id.all_messages ){
            Toast.makeText(this, "Opended", Toast.LENGTH_SHORT).show()
        }
        if(item.itemId == R.id.messages_sended ){
            val intent = Intent(this, AllEmailsController::class.java)
            intent.putExtra("mailbox_type", Mailboxes.SENT)
            startActivity(intent)
        }
        if(item.itemId == R.id.drafts ){
            val intent = Intent(this, AllEmailsController::class.java)
            intent.putExtra("mailbox_type", Mailboxes.DRAFTS)
            startActivity(intent)
        }
        if(item.itemId == R.id.log_out ){
            Credentials.EMAIL= ""
            Credentials.NAME = ""
            Credentials.PASSWORD = ""
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        if(item.itemId == R.id.deleted ){
            val intent = Intent(this, AllEmailsController::class.java)
            intent.putExtra("mailbox_type", Mailboxes.TRASH)
            startActivity(intent)
        }
        return true
    }





    /*
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

     */
}
