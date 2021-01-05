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
import kotlinx.android.synthetic.main.sign_in.*
import kotlinx.android.synthetic.main.sign_in.view.*

class SignIn : Fragment() {

    private lateinit var root: View
    private lateinit var email: String
    private lateinit var password: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.sign_in, container, false)

        // when user click on btnSignIn button call signIn method
        root.btnSignIn.setOnClickListener {
            email = sign_in_email.text.toString().trim()       // get email
            password = sign_in_password.text.toString().trim() // get password

            // to validate the user data before sign in
            if (validateEmail(email) and validatePassword(password))
                signIn(email, password)
        }

        // when user click on txtCreateAccount go to SignUp screen
        root.txtCreateAccount.setOnClickListener {
            replaceFragment(SignUp())
        }

        return root
    } // end of onCreateView method

    // this function to sign in
    private fun signIn (email: String, password: String)
    {
        MySharedPrefernces.prepare(activity!!)
        if (email == MySharedPrefernces.getUserEmail() &&
            password == MySharedPrefernces.getUserPassword())
        {
            // set user state as logged
            MySharedPrefernces.setUserState(true)
            // go to online users screen
            replaceFragment(OnlineUsers())
        } else if (MySharedPrefernces.getUserEmail() != email)
        {
            sign_in_email_error.error = "Email not found"
        } else if (MySharedPrefernces.getUserPassword() != email)
        {
            sign_in_password_error.error = "Password incorrect"
        }
    }

    // to check if the email is valid or not
    private fun validateEmail (email: String): Boolean
    {
        return if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            sign_in_email_error.error = "Email is invalid"
            false
        } else {
            sign_in_email_error.isErrorEnabled = false
            true
        }
    }

    // to check if the password is greater than 6 digit or not
    private fun validatePassword (password: String): Boolean
    {
        return if (password.length < 6)
        {
            sign_in_password_error.error = "Password is too short"
            false
        } else {
            sign_in_password_error.isErrorEnabled = false
            true
        }
    }

    // this function to switch between the fragments
    private fun replaceFragment (fragment: Fragment)
    {
        activity!!.supportFragmentManager.beginTransaction().replace(R.id.main_container, fragment).commit()
    }

}