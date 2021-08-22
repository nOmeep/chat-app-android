package com.example.messenger.datastuff

import android.widget.TextView
import com.example.messenger.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import de.hdodenhof.circleimageview.CircleImageView

class RecentMessagesItem(private val chatMessage: ChatMessage) : Item<ViewHolder>() {
    var companionUser : User? = null

    private val companionId : String = if (chatMessage.senderId == Firebase.auth.uid.toString()) chatMessage.receiverId else chatMessage.senderId
    private val companionDatabaseReference = Firebase.database.getReference("/users/$companionId")

    private val senderIdReference = Firebase.database.getReference("/users/${chatMessage.senderId}")

    override fun bind(viewHolder: ViewHolder, position: Int) {

        senderIdReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)

                if (chatMessage.message.length > 20) {
                    viewHolder.itemView.findViewById<TextView>(R.id.recentMessageLastMessageTextView).text = "${user?.username}: ${chatMessage.message.substring(0, 15)}..."
                } else {
                    viewHolder.itemView.findViewById<TextView>(R.id.recentMessageLastMessageTextView).text = "${user?.username}: ${chatMessage.message}"
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

        companionDatabaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                companionUser = snapshot.getValue(User::class.java)
                viewHolder.itemView.findViewById<TextView>(R.id.recentMessageUserNameTextView).text = companionUser?.username.toString()
                Picasso.get().load(companionUser?.profileImageUrl).into(viewHolder.itemView.findViewById<CircleImageView>(R.id.recentDialogUserImageView))
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    override fun getLayout(): Int {
        return R.layout.recent_messages_row
    }
}