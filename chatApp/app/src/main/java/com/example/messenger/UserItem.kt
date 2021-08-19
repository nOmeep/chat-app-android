package com.example.messenger

import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder

class UserItem: Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        // TODO
    }

    override fun getLayout(): Int {
        return R.layout.user_row_new_message
    }
}