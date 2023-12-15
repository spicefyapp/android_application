package com.dicoding.spicifyapplication.ui.chatbot

import androidx.lifecycle.ViewModel
import com.dicoding.spicifyapplication.data.repository.UploadRepository

class ChatBotViewModel(private val repository: UploadRepository) : ViewModel() {
    fun chatWithBot(chat: String) = repository.chatWithBot(chat)
}