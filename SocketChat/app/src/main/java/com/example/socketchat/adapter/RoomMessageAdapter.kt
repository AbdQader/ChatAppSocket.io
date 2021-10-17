package com.example.socketchat.adapter

import android.content.Context
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.socketchat.R
import com.example.socketchat.models.RoomMessage
import com.example.socketchat.sharedPreferences.MySharedPrefernces
import kotlinx.android.synthetic.main.receiver_text_message.view.*
import kotlinx.android.synthetic.main.sender_text_message.view.*

class RoomMessageAdapter(
    private var context: Context,
    private var roomMessages: ArrayList<RoomMessage>,
) : RecyclerView.Adapter<RoomMessageAdapter.RoomMessageViewHolder>() {

    // to define the message view holder type (send or receive)
    private val VIEW_TYPE_SEND_MESSAGE = 1
    private val VIEW_TYPE_RECEIVE_MESSAGE = 2

    open class RoomMessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        open fun bind(roomMessage: RoomMessage) {}
    }

    inner class SendMessageHolder(view: View) : RoomMessageAdapter.RoomMessageViewHolder(view) {
        private val senderMsg = view.txtSenderMsg!!
        private val senderMsgTime = view.txtSenderMsgTime!!

        override fun bind(roomMessage: RoomMessage) {
            senderMsg.text = roomMessage.message
            senderMsgTime.text = DateFormat.format("hh:mm a", roomMessage.messageTime)
        }
    }

    inner class ReceiveMessageHolder(view: View) : RoomMessageAdapter.RoomMessageViewHolder(view) {
        private val receiverMsg = view.txtReceiverMsg!!
        private val receiverName = view.txtReceiverName!!
        private val receiverMsgTime = view.txtReceiverMsgTime!!

        override fun bind(roomMessage: RoomMessage) {
            receiverMsg.text = roomMessage.message
            receiverName.text = roomMessage.sourceName
            receiverMsgTime.text = DateFormat.format("hh:mm a", roomMessage.messageTime)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val message = roomMessages[position]
        return if (message.sourceId == MySharedPrefernces.getUserId()) {
            VIEW_TYPE_SEND_MESSAGE
        } else {
            VIEW_TYPE_RECEIVE_MESSAGE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomMessageViewHolder {
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
        return roomMessages.size
    }

    override fun onBindViewHolder(holder: RoomMessageViewHolder, position: Int) {
        val message = roomMessages[position]
        holder.bind(message)
    }

}
