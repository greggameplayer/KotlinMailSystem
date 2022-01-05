package com.greggameplayer.kotlinmailsystem.controllers

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import android.widget.Button
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.navigation.NavigationView
import com.greggameplayer.kotlinmailsystem.AppExecutors
import com.greggameplayer.kotlinmailsystem.MainActivity
import com.greggameplayer.kotlinmailsystem.R
import com.greggameplayer.kotlinmailsystem.beans.Credentials
import com.greggameplayer.kotlinmailsystem.beans.Email
import com.greggameplayer.kotlinmailsystem.enums.Mailboxes
import kotlinx.android.synthetic.main.all_emails.*
import kotlinx.android.synthetic.main.drawer_content.*
import javax.mail.Flags
import javax.mail.Message

class AllEmailsController : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener{

    var emailController: EmailController = EmailController()
    lateinit var listEmails: Array<Message>
    lateinit var mailbox : Mailboxes
    lateinit var btNewMail : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.all_emails)

        //Drawer menu
        setSupportActionBar(toolbar)
        val toggle = ActionBarDrawerToggle(this, all_emails, toolbar, R.string.open, R.string.close)
        toggle.isDrawerIndicatorEnabled = true
        all_emails.addDrawerListener(toggle)
        toggle.syncState()
        test_nav_menu.setNavigationItemSelectedListener(this)

        //swiper listener
        iniRefreshListener()

        //to know which emails have to be loaded
        mailbox = intent.extras?.get("mailbox_type") as Mailboxes
        emailController.appExecutors = AppExecutors()
        loadEmailsAndRV()

        //Btn to redirect to sendmailActiviy
        btNewMail = findViewById(R.id.btn_new_email)
        btNewMail.setOnClickListener{
            this.finish()
            val intent = Intent(this, SendMail::class.java)
            startActivity(intent)
        }


    }

   private fun loadEmailsAndRV(){
       //To load emails
       emailController.retrieveAllEmails(mailbox){emails ->
           var listEmails = arrayListOf<Email>()
           emails.forEach { msg ->
               var email = Email()
               email.body = emailController.getTextFromMessage(msg).toString()
               email.from = msg.from.get(0).toString()
               email.subject = msg.subject
               email.read = msg.flags.contains(Flags.Flag.SEEN)
               listEmails.add(email)
           }
           listEmails.reverse()
           AppExecutors.MainThreadExecutor().execute {
               callAdapterRV(listEmails)
           }
       }
   }

    private fun callAdapterRV(listEmails: ArrayList<Email>) {
        //To pass items to the recycler view
        recycler_view.adapter = EmailsListAdapter(this, listEmails)
        recycler_view.layoutManager = LinearLayoutManager(this)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        //To load an other activity in function of menu
        if(item.itemId == R.id.all_messages ){
            val intent = Intent(this, AllEmailsController::class.java)
            intent.putExtra("mailbox_type",Mailboxes.INBOX)
            startActivity(intent)
        }
        if(item.itemId == R.id.messages_sended ){
            val intent = Intent(this, AllEmailsController::class.java)
            intent.putExtra("mailbox_type",Mailboxes.SENT)
            startActivity(intent)
        }
        if(item.itemId == R.id.drafts ){
            val intent = Intent(this, AllEmailsController::class.java)
            intent.putExtra("mailbox_type",Mailboxes.DRAFTS)
            startActivity(intent)
        }
        if(item.itemId == R.id.log_out ){
            //To disconnect user
            Credentials.EMAIL= ""
            Credentials.NAME = ""
            Credentials.PASSWORD = ""
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        if(item.itemId == R.id.deleted ){
            val intent = Intent(this, AllEmailsController::class.java)
            intent.putExtra("mailbox_type",Mailboxes.TRASH)
            startActivity(intent)
        }
        return true
    }

    fun iniRefreshListener() {
        //This function use swipper component, on up scroll it will reload recycler view (with a new api call)
            val swipeRefreshLayout=findViewById<SwipeRefreshLayout>(R.id.swipe_layout)
            swipeRefreshLayout.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
                loadEmailsAndRV()
                val handler = Handler()
                handler.postDelayed(Runnable {
                    if (swipeRefreshLayout.isRefreshing()) {
                        swipeRefreshLayout.setRefreshing(false)
                    }
                }, 3000)
            })
        }


}