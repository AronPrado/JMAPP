package com.javierprado.jmapp.view.adapters

import android.annotation.SuppressLint
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.javierprado.jmapp.R
import com.javierprado.jmapp.data.util.AnotherUtil
import com.javierprado.jmapp.data.util.AnotherUtil.Companion.MESSAGE_LEFT
import com.javierprado.jmapp.data.util.AnotherUtil.Companion.MESSAGE_RIGHT
import com.javierprado.jmapp.modal.Messages
import androidx.recyclerview.widget.DiffUtil

class MessageAdapter : RecyclerView.Adapter<MessageHolder>() {
    private var listOfMessage = listOf<Messages>()

    private val LEFT = 0
    private val RIGHT = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == RIGHT) {
            val view = inflater.inflate(R.layout.item_chat_derecha, parent, false)
            MessageHolder(view)
        } else {
            val view = inflater.inflate(R.layout.item_chat_izquierda, parent, false)
            MessageHolder(view)
        }
    }

    override fun getItemCount() = listOfMessage.size

    override fun onBindViewHolder(holder: MessageHolder, position: Int) {
        val message = listOfMessage[position]

        holder.messageText.visibility = View.VISIBLE
        holder.timeOfSent.visibility = View.VISIBLE

        holder.messageText.text = message.message
        holder.timeOfSent.text = message.time?.substring(0, 5) ?: ""
    }

    override fun getItemViewType(position: Int) =
        if (listOfMessage[position].sender == AnotherUtil.getUidLoggedIn()) RIGHT else LEFT

    fun setList(newList: List<Messages>) {
        this.listOfMessage = newList
    }
}

class MessageHolder(itemView: View) : RecyclerView.ViewHolder(itemView.rootView) {
    val messageText: TextView = itemView.findViewById(R.id.show_message)
    val timeOfSent: TextView = itemView.findViewById(R.id.timeView)
}