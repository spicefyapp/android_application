package com.dicoding.spicifyapplication.data.model

import java.util.Calendar

data class ChatModel(
    val chat: String,
    val isBot: Boolean = false,
    val timeStamp: Long = Calendar.getInstance().timeInMillis
)