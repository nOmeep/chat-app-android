package com.example.messenger

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MessagesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messages)

        authorizationCheck()
    }

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
                // LOGIC
            }
        }

        return super.onOptionsItemSelected(item)
    }
}