package com.greggameplayer.kotlinmailsystem.controllers

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.greggameplayer.kotlinmailsystem.R
import javax.mail.Message

class EmailsListAdapter(
    activity: Activity,
    private val data: Array<Message>
): RecyclerView.Adapter<EmailsListAdapter.VH>() {

    inner class VH(itemView: View):RecyclerView.ViewHolder(itemView){
        var message: String

        init {
            message= "test"
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

    }


}