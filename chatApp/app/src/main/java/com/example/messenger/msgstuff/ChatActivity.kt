package com.example.messenger.msgstuff

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.messenger.R

class ChatActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        supportActionBar?.title = "Chat with user"
    }
}