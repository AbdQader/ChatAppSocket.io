package com.example.socketchat.welcome_screens

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.example.socketchat.MainActivity
import com.example.socketchat.R
import kotlinx.android.synthetic.main.splash.*

class Splash : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash)

        logo.animate().alpha(1f).translationYBy(40f).duration = 3000
        txtLogo.animate().alpha(1f).translationYBy(40f).duration = 3000

        Handler().postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 3000)

    }
}