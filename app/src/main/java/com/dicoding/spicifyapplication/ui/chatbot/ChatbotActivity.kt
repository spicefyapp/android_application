package com.dicoding.spicifyapplication.ui.chatbot

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.spicifyapplication.data.model.ChatModel
import com.dicoding.spicifyapplication.databinding.ActivityChatbotBinding
import com.dicoding.spicifyapplication.helper.ResultState
import com.dicoding.spicifyapplication.ui.adapter.AdapterChatbot
import com.dicoding.spicifyapplication.ui.scan.ViewModelFactory

class ChatbotActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatbotBinding
    private val adapterChatBot = AdapterChatbot()

    private val viewModel by viewModels<ChatBotViewModel> {
        ViewModelFactory.getInstance(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityChatbotBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        binding.rvChatbot.layoutManager = LinearLayoutManager(this)
        binding.rvChatbot.adapter = adapterChatBot

        setupListener()

    }

    private fun setupListener() {

        binding.sendBtn.setOnClickListener {
            val userMessage = binding.edChat.text.toString().trim()
            if (userMessage.isNotEmpty()) {
                addMessage(ChatModel(userMessage, false))
                chatWithBot(userMessage)
                binding.edChat.text.clear()
            }
        }

        binding.btnBack.setOnClickListener { onBackPressed() }

    }

    private fun chatWithBot(chat : String) {
        viewModel.chatWithBot(chat).observe(this) { result ->
            if (result != null) {
                when (result) {
                    is ResultState.Loading -> {
                        showLoading(true)
                    }
                    is ResultState.Success -> {
                        val response = result.data
                        setReviewData(ChatModel(response.chat,true))
                        showLoading(false)
                    }

                    is ResultState.Error -> {
                        showLoading(false)
                        showToast("Terjadi kesalahan jaringan. Gagal menghubungkan server. Coba lagi")
                    }
                }
            }
        }
    }
    private fun setReviewData(chat: ChatModel) {
        addMessage(chat)
        binding.edChat.text.clear()
    }

    private fun addMessage(chat: ChatModel) {
        adapterChatBot.submitList(chat)
        binding.rvChatbot.scrollToPosition(adapterChatBot.itemCount - 1)
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}