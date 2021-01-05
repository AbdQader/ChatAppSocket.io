package com.example.socketchat.user_screens

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import com.example.socketchat.MyApp
import com.example.socketchat.R
import com.example.socketchat.adapter.MessageAdapter
import com.example.socketchat.keys.Keys
import com.example.socketchat.model.Message
import com.example.socketchat.model.User
import com.example.socketchat.sharedPreferences.MySharedPrefernces
import com.github.nkzawa.socketio.client.Socket
import kotlinx.android.synthetic.main.chat_screen.*
import kotlinx.android.synthetic.main.chat_screen.view.*
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

class ChatScreen : Fragment() {

    // for views
    private lateinit var root: View

    // for recycler view
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messages: ArrayList<Message>

    // for user
    private lateinit var user: User

    // for socket
    private lateinit var app: MyApp
    private var mSocket: Socket? = null

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.chat_screen, container, false)

        // to change action bar color
        activity!!.window.statusBarColor = ContextCompat.getColor(activity!!, R.color.actionBar)

        // initialize the ArrayList
        messages = ArrayList()

        // initialize the MessageAdapter
        messageAdapter = MessageAdapter(activity!!, messages)

        // initialize the socket
        app = activity!!.application as MyApp
        mSocket = app.getSocket()

        // to get user data from online users screen
        val bundle = arguments
        if (bundle != null)
            user = bundle.getParcelable(Keys.USER_BUNDLE)!!

        // to show the username in the toolbar
        root.chat_username.text = user.userName

        // connect recycler with adapter
        root.rv_chat_messages.adapter = messageAdapter

        // to receive the message from the other user
        receiveMessage()

        // when user click on btnSendMessage button send the message
        root.btnSendMessage.setOnClickListener {
            // to send the message to the other user
            sendMessage()
        }

        // when user click on back arrow
        root.chat_back_arrow.setOnClickListener {
            activity!!.onBackPressed()
        }

        return root
    } // end of onCreateView method

    // this function to send the message to the server
    private fun sendMessage ()
    {
        if (txtMessage.text.toString().trim().isNotEmpty())
        {
            // get message data and store it in a variables
            val msg = txtMessage.text.toString().trim()
            val sourceId = MySharedPrefernces.getUserId()
            val destinationId = user.userId
            val sourceName = MySharedPrefernces.getUserName()
            val destinationName = user.userName
            val msgTime = Calendar.getInstance().time

            // storage the message in a JSONObject
            val message = JSONObject()
            message.put(Keys.MESSAGE_CONTENT, msg)
            message.put(Keys.SOURCE_ID, sourceId)
            message.put(Keys.DESTINATION_ID, destinationId)
            message.put(Keys.SOURCE_NAME, sourceName)
            message.put(Keys.DESTINATION_Name, destinationName)
            message.put(Keys.MESSAGE_TIME, msgTime)
            // send the message to the server
            mSocket!!.emit(Keys.MESSAGE, message)

            // add the message to the messages array
            messages.add(Message(msg, sourceId, sourceName, msgTime))
            // notify the message adapter
            messageAdapter.notifyDataSetChanged()
            // scroll down to the last message sent
            rv_chat_messages.scrollToPosition(messages.size - 1)
            // to clean edit text box
            txtMessage.text.clear()
            // to hide keyboard
            val inputManager = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(activity!!.currentFocus!!.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        }
    } // end of sendMessage method

    // this function to receive the message from the server
    private fun receiveMessage ()
    {
        mSocket!!.on(Keys.MESSAGE) { args ->
            activity!!.runOnUiThread {
                try {
                    val message = args[0] as JSONObject
                    if (MySharedPrefernces.getUserId() == message.getString(Keys.DESTINATION_ID))
                    {
                        // add the message to the messages array
                        messages.add(Message(
                                message.getString(Keys.MESSAGE_CONTENT),
                                message.getString(Keys.SOURCE_ID),
                                message.getString(Keys.SOURCE_NAME),
                                Calendar.getInstance().time))
                        // notify the adapter
                        messageAdapter.notifyDataSetChanged()
                        // scroll down to the last message sent
                        rv_chat_messages.scrollToPosition(messages.size - 1)
                    }
                } catch (exception: Exception) {
                    Log.e("abd", "receiveMessage: ${exception.message}")
                }
            } // end of runOnUiThread
        } // end of mSocket
    } // end of receiveMessage method

}
