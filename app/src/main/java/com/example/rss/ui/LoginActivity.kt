package com.example.rss.ui

import com.google.firebase.auth.FirebaseAuth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.rss.MainActivity
import com.example.rss.R
import com.google.firebase.auth.*
import com.twitter.sdk.android.core.*
import kotlinx.android.synthetic.main.activity_login.*

private lateinit var auth: FirebaseAuth

val twitter_consumer_secret = "tBSL32VYXo2w6UA5eAaaIrKf9if4dFnY5aeuJv5yFQCUYpJe4l"
val twitter_consumer_key = "24vlrJX0MqYJbNNmlLZJmyKLR"

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initTwitter()

        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()
        initTwitterSignIn()
    }

    private fun initTwitter() {
        val config = TwitterConfig.Builder(this)
            .logger(DefaultLogger(Log.DEBUG))
            .twitterAuthConfig(TwitterAuthConfig(twitter_consumer_key, twitter_consumer_secret))
            .debug(true)
            .build()
        Twitter.initialize(config)
    }

    private fun initTwitterSignIn() {
        twitterLogInButton.callback = object : Callback<TwitterSession>() {
            override fun success(result: Result<TwitterSession>) {
                val session = TwitterCore.getInstance().sessionManager.activeSession
                handleTwitterLogin(session)
                Log.e("TWITTER", "Sign in succesful - going to MainActivity")
                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                startActivity(intent)
            }
            override fun failure(exception: TwitterException) {
                Log.e("TWITTER", "Sign in error")
            }
        }
    }

    private fun handleTwitterLogin(session: TwitterSession) {
        val credential = TwitterAuthProvider.getCredential(
            session.authToken.token,
            session.authToken.secret)
        auth.signInWithCredential(credential)
    }

    override fun onStart() {
        super.onStart()
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            Log.v("LOGIN", "Login Succesful - going to MainActivity")
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        twitterLogInButton.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)

    }


}
