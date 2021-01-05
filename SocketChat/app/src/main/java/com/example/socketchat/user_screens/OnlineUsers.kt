package com.example.socketchat.user_screens

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.socketchat.MyApp
import com.example.socketchat.R
import com.example.socketchat.adapter.UserAdapter
import com.example.socketchat.keys.Keys
import com.example.socketchat.login_register.SignIn
import com.example.socketchat.model.User
import com.example.socketchat.sharedPreferences.MySharedPrefernces
import com.github.nkzawa.emitter.Emitter
import com.github.nkzawa.socketio.client.Socket
import kotlinx.android.synthetic.main.online_users.view.*
import org.json.JSONArray
import org.json.JSONObject

class OnlineUsers : Fragment(), UserAdapter.OnItemClickListener {

    // for views
    private lateinit var root: View

    // for recycler view
    private lateinit var userAdapter: UserAdapter
    private lateinit var users: ArrayList<User>

    // for room users
    private lateinit var roomUsers: ArrayList<User>

    // for socket
    private lateinit var app: MyApp
    private var mSocket: Socket? = null

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.online_users, container, false)

        // for menu
        setHasOptionsMenu(true)

        // to change action bar color
        activity!!.window.statusBarColor = ContextCompat.getColor(activity!!, R.color.actionBar)

        // for menu (when user select item from the menu)
        root.online_users_toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId)
            {
                R.id.createRoom -> {
                    if (roomUsers.isNotEmpty())
                        sendUsersToChatRoom()
                    else
                        showDialog()
                }
                R.id.logout -> {
                    MySharedPrefernces.setUserState(false)
                    replaceFragmentWithoutStack(SignIn())
                }
            }
            onOptionsItemSelected(item)
        }

        // initialize the ArrayList
        users = ArrayList()
        roomUsers = ArrayList()

        // initialize the UserAdapter
        userAdapter = UserAdapter(activity!!, users, this)

        // initialize the socket
        app = activity!!.application as MyApp
        mSocket = app.getSocket()

        // connect recycler with adapter
        root.rv_onlin_users.adapter = userAdapter
        root.rv_onlin_users.setHasFixedSize(true)

        // when connect error
        mSocket!!.on(Socket.EVENT_CONNECT_ERROR) {
            activity!!.runOnUiThread {
                Log.e("EVENT_CONNECT_ERROR", "EVENT_CONNECT_ERROR: ")
            }
        }
        // when connect timeout
        mSocket!!.on(Socket.EVENT_CONNECT_TIMEOUT) {
            activity!!.runOnUiThread {
                Log.e("EVENT_CONNECT_TIMEOUT", "EVENT_CONNECT_TIMEOUT: ")
            }
        }
        // when disconnecting
        mSocket!!.on(Socket.EVENT_DISCONNECT) {
            activity!!.runOnUiThread {
                Log.e("onDisconnect", "Socket onDisconnect!")
            }
        }
        // when connecting
        mSocket!!.on(Socket.EVENT_CONNECT) {
            activity!!.runOnUiThread {
                Log.e("onConnect", "Socket Connected!")
                // send user data to the server
                mSocket!!.emit(Keys.USER_JOIN, sendUserDataToServer())
            }
        }

        // get online users from the server
        mSocket!!.on(Keys.USERS_CONNECTED, getOnlineUsers)

        // connect to the server
        mSocket!!.connect()

        return root
    } // end of onCreateView method

    override fun onResume() {
        super.onResume()
        // this fun i made on server to request user whenever i want
        // so i don't have to send user info or anything to get the online users
        // NOTE : the users will be sent through ("user-connected") channel
        mSocket!!.emit(Keys.REQUEST_USERS)
    }

    // when user click on any online users open chat with this user
    override fun onItemClick(position: Int) {
        val user = users[position]
        val bundle = Bundle()
        bundle.putParcelable(Keys.USER_BUNDLE, user)
        val chatScreen = ChatScreen()
        chatScreen.arguments = bundle
        replaceFragment(chatScreen)
    }

    // when user long click on any online users add user to room chat
    override fun onItemLongClick(position: Int) {
        roomUsers.add(users[position])
    }

    // this function to send list of users to the room chat screen
    private fun sendUsersToChatRoom ()
    {
        val bundle = Bundle()
        bundle.putParcelableArrayList(Keys.ROOM_USERS, roomUsers)
        val roomChat = RoomChat()
        roomChat.arguments = bundle
        replaceFragment(roomChat)
    }

    // to show the dialog for the user
    private fun showDialog ()
    {
        val alertDialog = AlertDialog.Builder(activity)
        alertDialog.setTitle("Select Users")
        alertDialog.setMessage("select users before creating the chat room, by long click on the user!")
        alertDialog.setCancelable(false)
        alertDialog.setIcon(R.drawable.ic_info)
        alertDialog.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }
        // show and create the alertDialog
        alertDialog.create().show()
    }

    // this function to send user data to the server
    private fun sendUserDataToServer () : JSONObject
    {
        val user = JSONObject()
        user.put(Keys.USER_ID, MySharedPrefernces.getUserId())
        user.put(Keys.USER_NAME, MySharedPrefernces.getUserName())
        user.put(Keys.USER_Email, MySharedPrefernces.getUserEmail())
        return user
    } // end of sendUserDataToServer method

    // to get the online users form the server
    private val getOnlineUsers = Emitter.Listener { args ->
        activity!!.runOnUiThread {
            try {
                val jsonArray = args[0] as JSONArray
                users.clear() // clear the users array
                for (i in 0 until jsonArray.length())
                {
                    val jsonObject = jsonArray.getJSONObject(i)
                    if (jsonObject.getString(Keys.USER_ID).equals(MySharedPrefernces.getUserId(), true))
                    {
                        continue
                    }
                    // remove duplicated users
                    for (u in 0 until users.size)
                    {
                        if(users[u].userId.equals(jsonObject.getString(Keys.USER_ID), true))
                            users.remove(users[u])
                    }
                    // add the user to users array
                    users.add(0, User(
                            jsonObject.getString(Keys.USER_ID),
                            jsonObject.getString(Keys.USER_NAME),
                            jsonObject.getString(Keys.USER_Email)
                    ))
                    if (users.isEmpty())
                    {
                        root.imgNoUsers.visibility = View.VISIBLE
                        root.txtNoUsers.visibility = View.VISIBLE
                    } else {
                        root.imgNoUsers.visibility = View.GONE
                        root.txtNoUsers.visibility = View.GONE
                    }
                }
                // notify the adapter
                userAdapter.notifyDataSetChanged()
            } catch (exception: Exception) {
                Log.e("abd", "getOnlineUsers: ${exception.message}")
            }
        } // end of runOnUiThread
    } // end of Emitter.Listener

    // this function to switch between the fragments
    private fun replaceFragment(fragment: Fragment)
    {
        activity!!.supportFragmentManager.beginTransaction()
                .addToBackStack(null).replace(R.id.main_container, fragment).commit()
    }
    private fun replaceFragmentWithoutStack(fragment: Fragment)
    {
        activity!!.supportFragmentManager.beginTransaction().replace(R.id.main_container, fragment).commit()
    }

    // to run the code in the main thread
    //Handler(Looper.getMainLooper()).post { run {} }

}
