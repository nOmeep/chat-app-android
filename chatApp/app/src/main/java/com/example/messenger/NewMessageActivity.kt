package com.example.messenger

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder

class NewMessageActivity : AppCompatActivity() {
    lateinit var chooseUserRecycler : RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)

        supportActionBar?.title = "Select"

        chooseUserRecycler = findViewById(R.id.newMessageRecyclerView)

        val adapter = GroupAdapter<ViewHolder>()
        adapter.add(UserItem())
        adapter.add(UserItem())
        adapter.add(UserItem())
        adapter.add(UserItem())
        chooseUserRecycler.adapter = adapter
    }
}