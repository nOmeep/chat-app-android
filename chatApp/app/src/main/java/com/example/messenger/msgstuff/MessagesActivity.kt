package com.example.messenger.msgstuff

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.messenger.R
import com.example.messenger.datastuff.User
import com.example.messenger.datastuff.UserItem
import com.example.messenger.helpfulfiles.onTouchAnimated
import com.example.messenger.registerandlogin.MainActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import java.util.*

class MessagesActivity : AppCompatActivity() {
    private lateinit var dialogChooseUserRecycler : RecyclerView
    private val recyclerAdapter = GroupAdapter<ViewHolder>()
    private var currentUid : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messages)
        supportActionBar?.title = "Recent messages"
        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#9556F1")))

        authorizationCheck()
    }

    // goTo log in activity(Log out)
    private fun exitFromActivity() {
        val loginActivity = Intent(this, MainActivity::class.java)
        loginActivity.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(loginActivity)
    }

    private fun authorizationCheck() {
        currentUid = Firebase.auth.uid

        if (currentUid == null) {
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
        }

        return super.onOptionsItemSelected(item)
    }

    private fun createNewMessageDialog() {
        val view = View.inflate(this@MessagesActivity, R.layout.new_messages_dialog_layout, null)

        val builder = AlertDialog.Builder(this@MessagesActivity)
        builder.setView(view)

        val dialog = builder.create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(true)

        dialogChooseUserRecycler = view.findViewById(R.id.dialogNewMessageRecyclerView)
        dialogChooseUserRecycler.adapter = recyclerAdapter

        recyclerAdapter.setOnItemClickListener { item, _ ->
            dialog.dismiss()

            val clickedUser = item as UserItem

            val startChattingIntent = Intent(this@MessagesActivity, ChatActivity::class.java)
            startChattingIntent.putExtra("PICKED_USER", clickedUser.user)
            startActivity(startChattingIntent)
        }

        dialog.show()

        getUsersFromDatabase()
    }

    private fun getUsersFromDatabase() {
        val databaseReference = Firebase.database.getReference("/users")

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
    }
}