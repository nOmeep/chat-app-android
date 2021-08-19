package com.example.messenger

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder

class MessagesActivity : AppCompatActivity() {
    private lateinit var dialogChooseUserRecycler : RecyclerView
    private val recyclerAdapter = GroupAdapter<ViewHolder>()

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
                //val newMessageActivity = Intent(this, NewMessageActivity::class.java)
                //startActivity(newMessageActivity)
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
        getUsersFromDatabase()

        dialog.setOnDismissListener {
            recyclerAdapter.clear()
        }

        dialog.show()
    }

    private fun getUsersFromDatabase() {

        val databaseReference = Firebase.database.getReference("/users")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach { child ->
                    val currUser = child.getValue(User::class.java)

                    if (currUser != null) {
                        recyclerAdapter.add(UserItem(currUser))
                    }
                }

                dialogChooseUserRecycler.adapter = recyclerAdapter
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MessagesActivity, "Something went wrong", Toast.LENGTH_SHORT).show()
            }
        })
    }
}