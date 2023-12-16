package com.dicoding.spicifyapplication.ui.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.spicifyapplication.data.network.response.RempahItem
import com.dicoding.spicifyapplication.databinding.ItemSpicelibRowBinding
import com.dicoding.spicifyapplication.ui.dashboard.spicelib.DetailSpiceLibActivity

class AdapterSpices : ListAdapter<RempahItem, AdapterSpices.ViewHolder>(DIFF_CALLBACK) {
    class ViewHolder (binding: ItemSpicelibRowBinding) : RecyclerView.ViewHolder(binding.root) {
        val imageStory = binding.ivSpice
        val tvNameStory = binding.tvNameSpice
//        val tvDescStory = binding.tvItemDescription

        fun bind(spice: RempahItem) {
            itemView.setOnClickListener {
                val intent = Intent(itemView.context, DetailSpiceLibActivity::class.java)
                intent.putExtra(DetailSpiceLibActivity.EXTRA_SPICE, spice)
                itemView.context.startActivity(intent)
            }
        }

    }




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSpicelibRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)

        Glide.with(holder.itemView.context).load(item.image).into(holder.imageStory)
        holder.tvNameStory.text = item.name
        holder.tvNameStory.text = item.name

        holder.bind(item)

    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<RempahItem>() {
            override fun areItemsTheSame(oldItem: RempahItem, newItem: RempahItem): Boolean {
                return oldItem.id == newItem.id
            }

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(oldItem: RempahItem, newItem: RempahItem): Boolean {
                return oldItem == newItem
            }
        }
    }

}