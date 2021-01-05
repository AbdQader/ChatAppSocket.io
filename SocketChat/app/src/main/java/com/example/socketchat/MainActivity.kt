package com.example.socketchat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.example.socketchat.keys.Keys
import com.example.socketchat.login_register.SignIn
import com.example.socketchat.sharedPreferences.MySharedPrefernces
import com.example.socketchat.user_screens.OnlineUsers
import com.github.nkzawa.socketio.client.Socket

class MainActivity : AppCompatActivity() {

    // for socket
    private lateinit var app: MyApp
    private var mSocket: Socket? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // initialize the socket
        app = application as MyApp
        mSocket = app.getSocket()

        // remove disconnected user
        mSocket!!.on("disconnect") {
            runOnUiThread {
                Log.e("abd", "disconnected")
            }
            mSocket!!.emit(Keys.REMOVE_USER, MySharedPrefernces.getUserId())
        }

        // prepare MySharedPreferences
        MySharedPrefernces.prepare(this)
        if (MySharedPrefernces.getUserState())
            replaceFragment(OnlineUsers())
        else
            replaceFragment(SignIn())

    } // end of onCreate method

    // this function to switch between the fragments
    private fun replaceFragment (fragment: Fragment)
    {
        supportFragmentManager.beginTransaction().replace(R.id.main_container, fragment).commit()
    }

}