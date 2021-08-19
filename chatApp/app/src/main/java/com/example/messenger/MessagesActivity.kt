package com.example.messenger

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MessagesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messages)
        supportActionBar?.title = "Recent messages"
        // check if user already authorized
        authorizationCheck()
    }

    // goTo log in activity(Log out)
    private fun exitFromActivity() {
        val loginActivity = Intent(this, MainActivity::class.java)
        loginActivity.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(loginActivity)
    }

    private fun authorizationCheck() {
        val uid = Firebase.auth.uid

        if (uid == null) {
            exitFromActivity()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_actions, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logOutMenuButton -> {
                Firebase.auth.signOut()
                exitFromActivity()
            }
            R.id.newMessageMenuButton -> {
                // NewMessageActivity checkout
                val newMessageActivity = Intent(this, NewMessageActivity::class.java)
                startActivity(newMessageActivity)
            }
        }

        return super.onOptionsItemSelected(item)
    }
}