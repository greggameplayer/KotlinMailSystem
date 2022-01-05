package com.greggameplayer.kotlinmailsystem.controllers

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.greggameplayer.kotlinmailsystem.R
import javax.mail.Message

class EmailsListAdapter(
    activity: Activity,
    private val data: Array<Message>
): RecyclerView.Adapter<EmailsListAdapter.VH>() {

    var ctx = activity

    inner class VH(itemView: View):RecyclerView.ViewHolder(itemView){
        var content: TextView
        var from: TextView
        var subject: TextView
        var card: CardView

        init {
            content= itemView.findViewById(R.id.email_content)
            from= itemView.findViewById(R.id.email_from)
            subject= itemView.findViewById(R.id.email_subject)
            card = itemView.findViewById(R.id.cardview)
        }

    }

    override fun getItemCount(): Int {
        return data.size
    }

    private val inflater : LayoutInflater

    init {
        this.inflater = activity.layoutInflater
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = inflater.inflate(R.layout.list_item_email, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(vh: VH, position: Int) {
        //vh.from.text = data.get(position).from.toString()
        vh.from.text = "victormarit.95@gmail.com"
        vh.subject.text = "Avancement du projet"
        vh.content.text = "Avancement du projet c'est la merde"
        //TODO check is openIf is already read
        //if(data.get(position) == true){}
        vh.card.setCardBackgroundColor(Color.WHITE);
        vh.from.setTypeface(Typeface.DEFAULT_BOLD)
        vh.subject.setTypeface(Typeface.DEFAULT_BOLD)

        vh.card.setOnClickListener{
            val intent = Intent(ctx, EmailDetails::class.java)
            intent.putExtra("from", "victormarit@gmail112.fr")
            intent.putExtra("content", "Avancement du projet")
            intent.putExtra("subject", "Avancement du projet c'est la merde")
            ctx.startActivity(intent)
        }
    }


}