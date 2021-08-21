package com.example.messenger.datastuff

import android.widget.TextView
import com.example.messenger.R
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder

class ChatCompanionItem(private val messageText : String) : Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.findViewById<TextView>(R.id.companionTextView).text = messageText
    }

    override fun getLayout(): Int {
        return R.layout.companion_chat_row
    }
}

class ChatUserOwnItem(private val messageText : String) : Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.findViewById<TextView>(R.id.userOwnTextView).text = messageText
    }

    override fun getLayout(): Int {
        return R.layout.user_own_chat_row
    }
}

