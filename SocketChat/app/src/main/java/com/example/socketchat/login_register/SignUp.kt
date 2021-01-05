package com.example.socketchat.login_register

import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.socketchat.R
import com.example.socketchat.sharedPreferences.MySharedPrefernces
import com.example.socketchat.user_screens.OnlineUsers
import kotlinx.android.synthetic.main.sign_up.*
import kotlinx.android.synthetic.main.sign_up.view.*

class SignUp : Fragment() {

    private lateinit var root: View
    private lateinit var username: String
    private lateinit var email: String
    private lateinit var password: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.sign_up, container, false)

        // when user click on btnSignUp button call signUp method
        root.btnSignUp.setOnClickListener {
            username = sign_up_username.text.toString().trim() // get username
            email = sign_up_email.text.toString().trim()       // get email
            password = sign_up_password.text.toString().trim() // get password

            // to validate the user data before sign up
            if (validateUsername(username) and validateEmail(email) and validatePassword(password))
                signUp(username, email, password)
        }

        // when user click on txtHaveAccount go to SignIn screen
        root.txtHaveAccount.setOnClickListener {
            replaceFragment(SignIn())
        }

        return root
    } // end of onCreateView method

    // this function to register a new user
    private fun signUp (username: String, email: String, password: String)
    {
        MySharedPrefernces.prepare(activity!!)
        // to give the user an id
        val userId = System.currentTimeMillis().toString()
        if (MySharedPrefernces.getUserEmail() == email)
        {
            sign_up_email_error.error = "This user has already logged in, try sign in"
        } else {
            MySharedPrefernces.setNewUser(userId, username, email, password)
            // set user state as logged
            MySharedPrefernces.setUserState(true)
            // go to online users screen
            replaceFragment(OnlineUsers())
        }
    }

    // to check if the username is greater than 15 digit or not
    private fun validateUsername (username: String): Boolean
    {
        return if (username.length > 15)
        {
            sign_up_username_error.error = "Username is too long"
            false
        } else {
            sign_up_username_error.isErrorEnabled = false
            true
        }
    }

    // to check if the email is valid or not
    private fun validateEmail (email: String): Boolean
    {
        return if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            sign_up_email_error.error = "Email is invalid"
            false
        } else {
            sign_up_email_error.isErrorEnabled = false
            true
        }
    }

    // to check if the password is greater than 6 digit or not
    private fun validatePassword (password: String): Boolean
    {
        return if (password.length < 6)
        {
            sign_up_password_error.error = "Password is too short"
            false
        } else {
            sign_up_password_error.isErrorEnabled = false
            true
        }
    }

    // this function to switch between the fragments
    private fun replaceFragment (fragment: Fragment)
    {
        activity!!.supportFragmentManager.beginTransaction().replace(R.id.main_container, fragment).commit()
    }

}