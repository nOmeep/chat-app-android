package com.example.messenger

import android.widget.TextView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import de.hdodenhof.circleimageview.CircleImageView

class UserItem(private val user : User): Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        if (user.uid == Firebase.auth.uid) {
            viewHolder.itemView.findViewById<TextView>(R.id.userNameRecycler).text = "It's me"
            Picasso.get().load(R.drawable.me_pic).into(viewHolder.itemView.findViewById<CircleImageView>(R.id.userImageRecycler))
        } else {
            viewHolder.itemView.findViewById<TextView>(R.id.userNameRecycler).text = user.username
            Picasso.get().load(user.profileImageUrl).into(viewHolder.itemView.findViewById<CircleImageView>(R.id.userImageRecycler))
        }
    }

    override fun getLayout(): Int {
        return R.layout.user_row_new_message
    }
}