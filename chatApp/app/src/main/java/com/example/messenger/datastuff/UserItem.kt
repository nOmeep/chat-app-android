package com.example.messenger.datastuff

import android.graphics.Paint
import android.graphics.Typeface
import android.widget.TextView
import com.example.messenger.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import de.hdodenhof.circleimageview.CircleImageView

class UserItem(val user : User): Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        if (user.uid == Firebase.auth.uid) {
            val userTextView = viewHolder.itemView.findViewById<TextView>(R.id.userNameRecycler)
            userTextView.text = user.username
            userTextView.setTypeface(null, Typeface.BOLD_ITALIC)
            userTextView.paintFlags = Paint.UNDERLINE_TEXT_FLAG

            Picasso.get().load(user.profileImageUrl).into(viewHolder.itemView.findViewById<CircleImageView>(
                R.id.userImageRecycler
            ))
        } else {
            viewHolder.itemView.findViewById<TextView>(R.id.userNameRecycler).text = user.username
            Picasso.get().load(user.profileImageUrl).into(viewHolder.itemView.findViewById<CircleImageView>(
                R.id.userImageRecycler
            ))
        }
    }

    override fun getLayout(): Int {
        return R.layout.user_row_new_message
    }
}