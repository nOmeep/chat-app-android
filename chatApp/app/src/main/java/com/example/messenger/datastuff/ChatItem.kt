package com.example.messenger.datastuff

import android.widget.ImageView
import android.widget.TextView
import com.example.messenger.R
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import de.hdodenhof.circleimageview.CircleImageView

class ChatCompanionItem(private val messageText : String, val user : User) : Item<ViewHolder>() {
    private val userImage = Picasso.get()

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.findViewById<TextView>(R.id.companionTextView).text = messageText
        val placeToLoad : CircleImageView = viewHolder.itemView.findViewById(R.id.companionProfileImage)
        userImage.load(user.profileImageUrl).into(placeToLoad)
    }

    override fun getLayout(): Int {
        return R.layout.companion_chat_row
    }
}

class ChatUserOwnItem(private val messageText : String, val user : User) : Item<ViewHolder>() {
    private val userOwnImage = Picasso.get()

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.findViewById<TextView>(R.id.userOwnTextView).text = messageText
        val placeToLoad : CircleImageView = viewHolder.itemView.findViewById(R.id.userOwnProfileImage)
        userOwnImage.load(user.profileImageUrl).into(placeToLoad)
    }

    override fun getLayout(): Int {
        return R.layout.user_own_chat_row
    }
}

