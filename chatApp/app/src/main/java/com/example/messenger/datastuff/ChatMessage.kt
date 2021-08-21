package com.example.messenger.datastuff

class ChatMessage(val id : String, val senderId : String, val receiverId : String, val message : String, val timeMark : Long) {
    constructor() : this("", "", "", "", -1)
}