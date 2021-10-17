package com.example.socketchat.adapter

import android.content.Context
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.socketchat.R
import com.example.socketchat.models.Message
import com.example.socketchat.sharedPreferences.MySharedPrefernces
import kotlinx.android.synthetic.main.receiver_text_message.view.*
import kotlinx.android.synthetic.main.sender_text_message.view.*

class MessageAdapter(
        private var context: Context,
        private val messages: ArrayList<Message>
) : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    // to define the message view holder type (send or receive)
    private val VIEW_TYPE_SEND_MESSAGE = 1
    private val VIEW_TYPE_RECEIVE_MESSAGE = 2

    open class MessageViewHolder (view: View) : RecyclerView.ViewHolder(view) {
        open fun bind(message: Message) {}
    }

    inner class SendMessageHolder(view: View) : MessageViewHolder(view) {
        private val senderMsg = view.txtSenderMsg!!
        private val senderMsgTime = view.txtSenderMsgTime!!

        override fun bind(message: Message) {
            senderMsg.text = message.message
            senderMsgTime.text = DateFormat.format("hh:mm a", message.messageTime)
        }
    }

    inner class ReceiveMessageHolder(view: View) : MessageViewHolder(view) {
        private val receiverMsg = view.txtReceiverMsg!!
        private val receiverName = view.txtReceiverName!!
        private val receiverMsgTime = view.txtReceiverMsgTime!!

        override fun bind(message: Message) {
            receiverMsg.text = message.message
            receiverName.text = message.userName
            receiverMsgTime.text = DateFormat.format("hh:mm a", message.messageTime)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val message = messages[position]
        return if (message.userId == MySharedPrefernces.getUserId())
            VIEW_TYPE_SEND_MESSAGE
        else
            VIEW_TYPE_RECEIVE_MESSAGE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        return if (viewType == VIEW_TYPE_SEND_MESSAGE)
        {
            val view = LayoutInflater.from(context).inflate(R.layout.sender_text_message, parent, false)
            SendMessageHolder(view)
        } else {
            val view = LayoutInflater.from(context).inflate(R.layout.receiver_text_message, parent, false)
            ReceiveMessageHolder(view)
        }
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages[position]
        holder.bind(message)
    }

}
