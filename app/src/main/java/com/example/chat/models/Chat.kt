package com.example.chat.models

data class Chat(
    val personId : String ,
    val person: String ,
    val profile : String ,
    val lastMessage : String,
    val time : String,
    val isSender : Boolean
)
