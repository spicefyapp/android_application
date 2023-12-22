package com.dicoding.spicifyapplication.ui.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.spicifyapplication.data.model.ProductSpiceModel
import com.dicoding.spicifyapplication.data.network.response.RempahItem
import com.dicoding.spicifyapplication.data.network.response.SpiceItem
import com.dicoding.spicifyapplication.databinding.ItemSpicelibRowBinding
import com.dicoding.spicifyapplication.databinding.ItemSpicemartRowBinding
import com.dicoding.spicifyapplication.ui.dashboard.spicelib.DetailSpiceLibActivity
import com.dicoding.spicifyapplication.ui.dashboard.spicemart.DetailSpiceMartActivity
import java.io.IOException
import java.util.Locale

class AdapterSpiceMart : ListAdapter<SpiceItem, AdapterSpiceMart.ViewHolder>(DIFF_CALLBACK) {

    class ViewHolder (binding: ItemSpicemartRowBinding) : RecyclerView.ViewHolder(binding.root) {
        var ivImage = binding.ivProduct
        var tvName = binding.tvNameProduct
        var tvPrice = binding.tvPriceProduct
        var tvLoc = binding.tvLocProduct

        fun bind(spice: SpiceItem) {
            val geocoder = Geocoder(itemView.context, Locale.getDefault())

            fun getAddressDetails(lat: Double, lon: Double): String {
                try {
                    val addresses: MutableList<Address>? = geocoder.getFromLocation(lat, lon, 1)

                    if (addresses?.isNotEmpty() == true) {
                        val address: Address = addresses[0]

                        val thoroughfare = address.thoroughfare ?: ""
                        val subLocality = address.subLocality ?: ""
                        val locality = address.locality ?: ""

                        return "$thoroughfare $subLocality, $locality"
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                return "Alamat tidak ditemukan"
            }

            val alamatJalan = getAddressDetails(spice.lat!!, spice.lan!!)

            Glide.with(itemView).load(spice.image)
                .into(ivImage)
            tvName.text = spice.name
            tvPrice.text = spice.price
            tvLoc.text = alamatJalan

            itemView.setOnClickListener {
                val intent = Intent(itemView.context, DetailSpiceMartActivity::class.java)
                intent.putExtra("Image", spice.image)
                intent.putExtra("Description", spice.description)
                intent.putExtra("Title", spice.name)
                intent.putExtra("Price", spice.price)
                intent.putExtra("Wa", spice.noWA)
                intent.putExtra("Lat", spice.lat)
                intent.putExtra("Lon", spice.lan)
                itemView.context.startActivity(intent)
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSpicemartRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AdapterSpiceMart.ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<SpiceItem>() {
            override fun areItemsTheSame(oldItem: SpiceItem, newItem: SpiceItem): Boolean {
                return oldItem.id == newItem.id
            }

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(oldItem: SpiceItem, newItem: SpiceItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}