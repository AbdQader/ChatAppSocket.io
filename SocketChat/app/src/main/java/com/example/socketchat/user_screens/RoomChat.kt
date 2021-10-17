package com.example.socketchat.user_screens

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.socketchat.MyApp
import com.example.socketchat.R
import com.example.socketchat.adapter.RoomMessageAdapter
import com.example.socketchat.keys.Keys
import com.example.socketchat.models.RoomMessage
import com.example.socketchat.models.User
import com.example.socketchat.sharedPreferences.MySharedPrefernces
import com.github.nkzawa.socketio.client.Socket
import kotlinx.android.synthetic.main.chat_screen.view.*
import kotlinx.android.synthetic.main.room_chat.*
import kotlinx.android.synthetic.main.room_chat.view.*
import kotlinx.android.synthetic.main.room_chat.view.imgNoMessages
import kotlinx.android.synthetic.main.room_chat.view.txtNoMessages
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

class RoomChat : Fragment() {

    // for views
    private lateinit var root: View

    // for recycler view
    private lateinit var roomMessageAdapter: RoomMessageAdapter
    private lateinit var roomMessages: ArrayList<RoomMessage>

    // for user list
    private lateinit var users: ArrayList<User>
    // for users id and name
    private lateinit var idList: JSONArray
    private lateinit var nameList: JSONArray

    // for socket
    private lateinit var app: MyApp
    private var mSocket: Socket? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.room_chat, container, false)

        // initialize the Arrays and JSONArray
        roomMessages = ArrayList()
        users = ArrayList()
        idList = JSONArray()
        nameList = JSONArray()

        // initialize the MessageAdapter
        roomMessageAdapter = RoomMessageAdapter(activity!!, roomMessages)

        // initialize the socket
        app = activity!!.application as MyApp
        mSocket = app.getSocket()

        // to get users list data from online users screen
        val bundle = arguments
        if (bundle != null)
            users = bundle.getParcelableArrayList(Keys.ROOM_USERS)!!

        // connect recycler with adapter
        root.rv_room_chat_messages.adapter = roomMessageAdapter

        // to receive the room message from the other users
        receiveRoomMessage()

        // when user click on btnSendRoomChatMessage button send the message
        root.btnSendRoomChatMessage.setOnClickListener {
            // to send the room message to the other users
            sendRoomMessage()
        }

        // when user click on back arrow
        root.room_chat_back_arrow.setOnClickListener {
            activity!!.onBackPressed()
        }

        return root
    } // end of onCreateView method

    // this function to send the room message to the server
    private fun sendRoomMessage ()
    {
        if (txtRoomChatMessage.text.toString().trim().isNotEmpty())
        {
            // store the users list id in idList array
            users.forEach { user ->
                idList.put(user.userId!!)
            }
            // store the users list name in nameList array
            users.forEach { user ->
                nameList.put(user.userName!!)
            }
            // get room message data
            val msg = txtRoomChatMessage.text.toString().trim()
            val sourceId = MySharedPrefernces.getUserId()
            val sourceName = MySharedPrefernces.getUserName()
            val destinationId = idList
            val destinationName = nameList
            val msgTime = Calendar.getInstance().time

            // storage the room message data in JSONObject
            val roomMessage = JSONObject()
            roomMessage.put(Keys.MESSAGE_CONTENT, msg)
            roomMessage.put(Keys.SOURCE_ID, sourceId)
            roomMessage.put(Keys.SOURCE_NAME, sourceName)
            roomMessage.put(Keys.DESTINATION_ID, destinationId)
            roomMessage.put(Keys.DESTINATION_Name, destinationName)
            roomMessage.put(Keys.MESSAGE_TIME, msgTime)
            // send the room message to the server
            mSocket!!.emit(Keys.ROOM_MESSAGE, roomMessage)

            // add the RoomMessage to the roomMessages array
            roomMessages.add(RoomMessage(msg, sourceId, sourceName, destinationId, destinationName, msgTime))
            // notify the roomMessages adapter
            roomMessageAdapter.notifyDataSetChanged()
            // scroll down to the last message sent
            rv_room_chat_messages.scrollToPosition(roomMessages.size - 1)
            // to clean edit text box
            txtRoomChatMessage.text.clear()
            // to hide the text & image placeholder
            root.imgNoMessages.visibility = View.GONE
            root.txtNoMessages.visibility = View.GONE
            // to hide keyboard
            //val inputManager = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            //inputManager.hideSoftInputFromWindow(activity!!.currentFocus!!.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        }
    } // end of sendRoomMessage method

    // this function to receive the message from the server
    private fun receiveRoomMessage ()
    {
        mSocket!!.on(Keys.ROOM_MESSAGE) { args ->
            activity!!.runOnUiThread {
                try {
                    val message = args[0] as JSONObject
                    val destinationId = message.getJSONArray(Keys.DESTINATION_ID)
                    for (i in 0 until destinationId.length())
                    {
                        if (MySharedPrefernces.getUserId() == destinationId[i])
                        {
                            // add the message to the room messages list
                            roomMessages.add(
                                RoomMessage(
                                    message.getString(Keys.MESSAGE_CONTENT),
                                    message.getString(Keys.SOURCE_ID),
                                    message.getString(Keys.SOURCE_NAME),
                                    message.getJSONArray(Keys.DESTINATION_ID),
                                    message.getJSONArray(Keys.DESTINATION_Name),
                                    Calendar.getInstance().time
                                )
                            )

                            // to hide the text & image placeholder
                            root.imgNoMessages.visibility = View.GONE
                            root.txtNoMessages.visibility = View.GONE

                            // notify the adapter
                            roomMessageAdapter.notifyDataSetChanged()
                            // scroll down to the last message sent
                            rv_room_chat_messages.scrollToPosition(roomMessages.size - 1)
                            break // return@on
                        }
                    }

                } catch (exception: Exception) {
                    Log.e("abd", "receiveRoomMessage: ${exception.message}")
                }
            } // end of runOnUiThread
        } // end of mSocket
    } // end of receiveRoomMessage method

}
