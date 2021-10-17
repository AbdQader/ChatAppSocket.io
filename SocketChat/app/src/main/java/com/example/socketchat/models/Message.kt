package com.example.socketchat.models

import java.util.*

data class Message (
    var message: String,
    var userId: String,
    var userName: String,
    var messageTime: Date
)