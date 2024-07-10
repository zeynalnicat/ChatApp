package com.example.chat.models

data class Account (
    val id : String,
    val name :String ,
    val profile: String ,
    var isChecked: Boolean = false
)