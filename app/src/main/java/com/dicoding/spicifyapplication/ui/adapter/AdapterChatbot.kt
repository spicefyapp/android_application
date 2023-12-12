package com.dicoding.spicifyapplication.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.spicifyapplication.data.model.ChatModel
import com.dicoding.spicifyapplication.databinding.ItemChatbotRowBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AdapterChatbot: RecyclerView.Adapter<AdapterChatbot.MyViewHolder>() {
    private val list = ArrayList<ChatModel>()

    inner class MyViewHolder(private val binding: ItemChatbotRowBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(chat: ChatModel) {

            if (!chat.isBot) {
                binding.leftChatView.visibility = View.GONE
                binding.rightChatView.visibility = View.VISIBLE
                binding.rightChatTextView.text = chat.chat
                binding.rightTimeTextView.text = getTimeString(chat.timeStamp)

            } else {
                binding.rightChatView.visibility = View.GONE
                binding.leftChatView.visibility = View.VISIBLE
                binding.leftChatTextView.text = chat.chat
                binding.leftTimeTextView.text = getTimeString(chat.timeStamp)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            ItemChatbotRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(list[position])
    }

    fun submitList(newList: ChatModel) {
        list.add(newList)
        notifyDataSetChanged()
    }

    private fun getTimeString(timeStamp: Long): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeStamp
        val dateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }
}