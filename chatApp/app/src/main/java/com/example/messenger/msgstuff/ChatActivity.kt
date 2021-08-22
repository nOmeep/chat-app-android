package com.example.messenger.msgstuff

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.messenger.R
import com.example.messenger.datastuff.ChatCompanionItem
import com.example.messenger.datastuff.ChatMessage
import com.example.messenger.datastuff.ChatUserOwnItem
import com.example.messenger.datastuff.User
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder

class ChatActivity : AppCompatActivity() {
    private var chattingWithUser : User? = null
    private var self : User? = null

    private lateinit var sendButton : Button
    private lateinit var messageEditText : EditText

    private lateinit var chatRecycler : RecyclerView
    private val adapter = GroupAdapter<ViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        chattingWithUser = intent.getParcelableExtra("PICKED_USER")
        getSelf()

        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#9556F1")))
        supportActionBar?.title = chattingWithUser?.username

        init()
        startMessaging()
    }

    private fun getSelf() {
        val databaseReference = Firebase.database.getReference("/users/${Firebase.auth.uid}")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                self = snapshot.getValue(User::class.java)
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun init() {
        chatRecycler = findViewById(R.id.chatMessageRecycler)
        chatRecycler.adapter = adapter

        sendButton = findViewById(R.id.chatSendButton)
        messageEditText = findViewById(R.id.messageEditText)

        sendButton.setOnClickListener {
            if (messageEditText.text.toString().isNotEmpty()) {
                sendMessage()
                messageEditText.text.clear()
            } else {
                Toast.makeText(this, "fill the message gap", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sendMessage() {
        val senderId = Firebase.auth.uid.toString()
        val receiverId = chattingWithUser?.uid.toString()
        val message = messageEditText.text.toString()

        val databaseReference = Firebase.database.getReference("/messages/$senderId/$receiverId").push()
        val additionalReference = Firebase.database.getReference("/messages/$receiverId/$senderId").push()

        val currentMessage = ChatMessage(databaseReference.key!!, senderId, receiverId, message, System.currentTimeMillis() / 1000)

        databaseReference.setValue(currentMessage).addOnSuccessListener {
            Toast.makeText(this, "sent to database", Toast.LENGTH_SHORT).show()
        }

        if (senderId != receiverId) {
            additionalReference.setValue(currentMessage)
        }

        Firebase.database.getReference("/recent_messages/$senderId/$receiverId").setValue(currentMessage)
        Firebase.database.getReference("/recent_messages/$receiverId/$senderId").setValue(currentMessage)
    }

    // Fetch messages

    private fun startMessaging() {
        val senderId = Firebase.auth.uid.toString()
        val receiverId = chattingWithUser?.uid.toString()

        val databaseReference = Firebase.database.getReference("/messages/$senderId/$receiverId")
        databaseReference.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val currChatMessage = snapshot.getValue(ChatMessage::class.java)

                if (currChatMessage != null) {
                    if (currChatMessage.senderId == Firebase.auth.uid) {
                        adapter.add(ChatUserOwnItem(currChatMessage.message, self!!))
                        chatRecycler.scrollToPosition(adapter.itemCount - 1)
                    } else {
                        adapter.add(ChatCompanionItem(currChatMessage.message, chattingWithUser!!))
                    }
                }

                chatRecycler.scrollToPosition(adapter.itemCount - 1)
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildRemoved(snapshot: DataSnapshot) {

            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }
}