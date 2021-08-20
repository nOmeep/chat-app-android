package com.example.messenger.datastuff

class User(val uid : String, val username : String, val profileImageUrl : String) {
    constructor() : this("", "", "")
}