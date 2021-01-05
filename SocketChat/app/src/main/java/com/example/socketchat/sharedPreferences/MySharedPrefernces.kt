package com.example.socketchat.sharedPreferences

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity

class MySharedPrefernces {

    companion object
    {
        // context
        private var context: Context? = null

        // object from shared preferences
        private var sharedPrefernces: SharedPreferences? = null

        // keys
        private var USER_ID = "userId"
        private var USER_NAME = "userName"
        private var USER_EMAIL = "userEmail"
        private var USER_PASSWORD = "userPassword"
        private var USER_STATE_KEY = "user state key"

        /** this fun prepare the shared preferences so u can use it later
         * NOTE: u cant use share pref without call this method (it will cause null pointer exception)*/
        fun prepare(context: Context)
        {
            this.context = context
            sharedPrefernces = context.applicationContext
                .getSharedPreferences("MyPrefs", AppCompatActivity.MODE_PRIVATE)
        }

        /** this fun to add new user */
        fun setNewUser(userId: String, userName: String, userEmail: String, userPassword: String)
        {
            getEditor().putString(USER_ID, userId).commit()
            getEditor().putString(USER_NAME, userName).commit()
            getEditor().putString(USER_EMAIL, userEmail).commit()
            getEditor().putString(USER_PASSWORD, userPassword).commit()
        }

        /** return user id */
        fun getUserId (): String = sharedPrefernces!!.getString(USER_ID, "UNKNOWN")!!
        /** return user name */
        fun getUserName (): String = sharedPrefernces!!.getString(USER_NAME, "UNKNOWN")!!
        /** return user email */
        fun getUserEmail (): String = sharedPrefernces!!.getString(USER_EMAIL, "UNKNOWN")!!
        /** return user password */
        fun getUserPassword (): String = sharedPrefernces!!.getString(USER_PASSWORD, "UNKNOWN")!!

        /** user logged state (is the user still logged or not) */
        fun setUserState(isLogged: Boolean)
        {
            getEditor().putBoolean(USER_STATE_KEY, isLogged).commit()
        }

        /** return user logged state */
        fun getUserState () : Boolean
        {
            return sharedPrefernces!!.getBoolean(USER_STATE_KEY, false)
        }

        /** return editor of my shared preferences */
        private fun getEditor() : SharedPreferences.Editor = sharedPrefernces!!.edit()

    }
}
