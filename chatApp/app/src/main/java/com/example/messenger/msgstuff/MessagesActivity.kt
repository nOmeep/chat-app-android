package com.example.messenger.msgstuff

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.messenger.R
import com.example.messenger.datastuff.ChatMessage
import com.example.messenger.datastuff.RecentMessagesItem
import com.example.messenger.datastuff.User
import com.example.messenger.datastuff.UserItem
import com.example.messenger.helpfulfiles.onTouchAnimated
import com.example.messenger.registerandlogin.MainActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.new_messages_dialog_layout.*
import java.util.*
import kotlin.collections.HashMap

class MessagesActivity : AppCompatActivity() {
    private lateinit var recentMessagesRecyclerView : RecyclerView
    private val adapterForRecentMessagesRecycler = GroupAdapter<ViewHolder>()

    private lateinit var dialogChooseUserRecycler : RecyclerView
    private val recyclerChooseUserAdapter = GroupAdapter<ViewHolder>()

    private var currentUid : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messages)
        supportActionBar?.title = "Recent messages"
        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#9556F1")))

        authorizationCheck()

        createRecentMessagesRecycler()
    }

    private fun authorizationCheck() {
        currentUid = Firebase.auth.uid

        if (currentUid == null) {
            exitFromActivity()
        }
    }

    private fun exitFromActivity() {
        val loginActivity = Intent(this, MainActivity::class.java)
        loginActivity.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(loginActivity)
    }

    private fun createRecentMessagesRecycler() {
        recentMessagesRecyclerView = findViewById(R.id.recentMessagesRecycler)
        recentMessagesRecyclerView.adapter = adapterForRecentMessagesRecycler

        seRecyclerAdapterItemClickListener(adapterForRecentMessagesRecycler)

        getRecentMessages()
    }

    private fun seRecyclerAdapterItemClickListener(tmpAdapter: GroupAdapter<ViewHolder>) {
        tmpAdapter.setOnItemClickListener { item, _ ->
            val user = item as RecentMessagesItem

            val startChattingIntent = Intent(this@MessagesActivity, ChatActivity::class.java)
            startChattingIntent.putExtra("PICKED_USER", user.companionUser)
            startActivity(startChattingIntent)
        }
    }

    val recentMessagesMap = HashMap<String, ChatMessage>()
    private fun mapRefreshMagic() {
        adapterForRecentMessagesRecycler.clear()

        recentMessagesMap.forEach { msg ->
            adapterForRecentMessagesRecycler.add(RecentMessagesItem(msg.value))
        }
    }


    private fun getRecentMessages() {
        val recentMessagesDatabase = Firebase.database.getReference("/recent_messages/$currentUid")
        recentMessagesDatabase.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val message = snapshot.getValue(ChatMessage::class.java)

                if (message != null) {
                    recentMessagesMap[snapshot.key!!] = message
                    mapRefreshMagic()
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val message = snapshot.getValue(ChatMessage::class.java)
                if (message != null) {
                    mapRefreshMagic()
                    adapterForRecentMessagesRecycler.add(RecentMessagesItem(message))
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {

            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_actions, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logOutMenuButton -> {
                val view = View.inflate(this@MessagesActivity, R.layout.confirm_log_out_dialog, null)
                val builder =  AlertDialog.Builder(this@MessagesActivity)
                builder.setView(view)

                val dialog = builder.create()
                dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
                dialog.setCancelable(true)

                val yesButton : Button = view.findViewById(R.id.yesButtonConfirmDialog)
                val noButton : Button = view.findViewById(R.id.noButtonConfirmDialog)

                onTouchAnimated(yesButton, noButton)

                yesButton.setOnClickListener {
                    Firebase.auth.signOut()
                    exitFromActivity()
                }

                noButton.setOnClickListener {
                    dialog.dismiss()
                }

                dialog.show()
            }
            R.id.newMessageMenuButton -> {
                createNewMessageDialog()
            }
            R.id.addFriendMenuButton -> {
                //createAddFriendDialog()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun createAddFriendDialog() {
        val view = View.inflate(this@MessagesActivity, R.layout.add_friend_dialog_layout, null)
        val builder = AlertDialog.Builder(this@MessagesActivity)
        builder.setView(view)
        val dialog = builder.create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(true)

        val addNewFriendEditText : EditText = view.findViewById(R.id.addFriendEditText)
        val addNewFriendByIdButton : Button = view.findViewById(R.id.addByUidButton)

        addNewFriendByIdButton.setOnClickListener {
            if (addNewFriendEditText.text.toString().isNotEmpty()) {
                addFriendToDatabase(addNewFriendEditText.text.toString())
            }
        }

        dialog.show()
    }

    private fun addFriendToDatabase(uid : String) {
        val friendsDatabaseReference = Firebase.database.getReference("/friend-list/$currentUid")
        friendsDatabaseReference.child(uid).setValue(uid)
    }

    private fun createNewMessageDialog() {
        val view = View.inflate(this@MessagesActivity, R.layout.new_messages_dialog_layout, null)

        val builder = AlertDialog.Builder(this@MessagesActivity)
        builder.setView(view)

        val dialog = builder.create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(true)

        dialogChooseUserRecycler = view.findViewById(R.id.dialogNewMessageRecyclerView)
        dialogChooseUserRecycler.adapter = recyclerChooseUserAdapter

        recyclerChooseUserAdapter.setOnItemClickListener { item, _ ->
            dialog.dismiss()

            val clickedUser = item as UserItem

            val startChattingIntent = Intent(this@MessagesActivity, ChatActivity::class.java)
            startChattingIntent.putExtra("PICKED_USER", clickedUser.user)
            startActivity(startChattingIntent)
        }

        dialog.setOnDismissListener {
            recyclerChooseUserAdapter.clear()
        }

        dialog.show()

        getUsersFromDatabase()
    }

    private fun getUsersFromDatabase() {
        val databaseReference = Firebase.database.getReference("/users/")

        databaseReference.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val currUser = snapshot.getValue(User::class.java)

                if (currUser != null && currUser.uid != Firebase.auth.uid.toString()) {
                    recyclerChooseUserAdapter.add(UserItem(currUser))
                }
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
/*
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                recyclerAdapter.clear()

                snapshot.children.forEach { child ->
                    val currUser = child.getValue(User::class.java)
                    if (currUser != null) {
                        recyclerAdapter.add(UserItem(currUser))
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MessagesActivity, "Something went wrong", Toast.LENGTH_SHORT).show()
            }
        })

 */
    }
}